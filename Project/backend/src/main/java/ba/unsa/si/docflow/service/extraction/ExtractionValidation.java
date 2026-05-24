package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ExtractionValidation {

    private static final BigDecimal MIN_CONFIDENCE_FOR_AUTO_CONFIRM = new BigDecimal("0.70");

    private static final BigDecimal AMOUNT_TOTAL_TOLERANCE = new BigDecimal("0.01");

    private static final Set<String> REQUIRED_INVOICE_FIELDS =
            Set.of("invoice_id", "invoice_date", "supplier_name", "total_amount", "currency");

    /*
     * Expense/receipt parser field names can vary depending on the document and parser output.
     * We accept either receipt_date or expense_date as the required date field.
     */
    private static final Set<String> REQUIRED_RECEIPT_FIELDS =
            Set.of("supplier_name", "total_amount", "currency");

    private static final Set<String> RECEIPT_DATE_FIELD_ALIASES =
            Set.of("receipt_date", "expense_date", "transaction_date", "purchase_date");

    private static final Set<String> REQUIRED_BANK_STATEMENT_FIELDS = Set.of("account_number");

    private static final Map<DocumentType, Set<String>> REQUIRED_FIELDS_BY_DOCUMENT_TYPE =
            Map.of(
                    DocumentType.INVOICE, REQUIRED_INVOICE_FIELDS,
                    DocumentType.RECEIPT, REQUIRED_RECEIPT_FIELDS,
                    DocumentType.BANK_STATEMENT, REQUIRED_BANK_STATEMENT_FIELDS);

    private static final Set<String> BANK_STATEMENT_IDENTITY_FIELDS =
            Set.of("bank_name", "client_name", "account_holder_name", "customer_name");

    private static final Set<String> BANK_STATEMENT_ACTIVITY_FIELDS =
            Set.of(
                    "statement_date",
                    "statement_start_date",
                    "statement_end_date",
                    "starting_balance",
                    "ending_balance",
                    "opening_balance",
                    "closing_balance",
                    "current_balance",
                    "table_item",
                    "table_item/transaction_date",
                    "table_item/transaction_deposit",
                    "table_item/transaction_withdrawal",
                    "table_item/transaction_amount",
                    "transaction_date",
                    "transaction_deposit",
                    "transaction_withdrawal",
                    "transaction_amount");

    private static final Set<String> DATE_FIELDS =
            Set.of(
                    "invoice_date",
                    "due_date",
                    "delivery_date",
                    "issue_date",
                    "payment_due_date",
                    "receipt_date",
                    "expense_date",
                    "transaction_date",
                    "purchase_date",
                    "statement_date",
                    "statement_start_date",
                    "statement_end_date",
                    "table_item/transaction_date");

    private static final Set<String> NUMERIC_FIELDS =
            Set.of(
                    "net_amount",
                    "vat_amount",
                    "total_amount",
                    "total_tax_amount",
                    "tax_amount",
                    "subtotal_amount",
                    "amount",
                    "price",
                    "unit_price",
                    "quantity",
                    "qty",
                    "starting_balance",
                    "ending_balance",
                    "opening_balance",
                    "closing_balance",
                    "current_balance",
                    "transaction_deposit",
                    "transaction_withdrawal",
                    "transaction_amount",
                    "table_item/transaction_deposit",
                    "table_item/transaction_withdrawal",
                    "table_item/transaction_amount");

    private static final List<DateTimeFormatter> ACCEPTED_DATE_FORMATS =
            List.of(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu")
                            .withResolverStyle(ResolverStyle.STRICT),
                    DateTimeFormatter.ofPattern("dd/MM/uuuu")
                            .withResolverStyle(ResolverStyle.STRICT));

    private static final Set<DocumentStatus> FIELD_EDIT_ALLOWED_STATUSES =
            Set.of(DocumentStatus.EXTRACTED, DocumentStatus.NEEDS_CORRECTION);

    private static final Pattern CUSTOM_FIELD_NAME_PATTERN =
            Pattern.compile("^custom\\.[a-z][a-z0-9_]*$");

    private static final Set<String> ALLOWED_CANONICAL_FIELD_NAMES =
            buildAllowedCanonicalFieldNames();

    public void validateRequiredFields(ExtractionEntity extraction) {
        ValidationErrors errors = new ValidationErrors();

        validateExtractionFields(extraction, errors);
        validateLowConfidenceFieldsAreCorrected(extraction, errors);

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public Set<String> getRequiredFields(DocumentType documentType) {
        return REQUIRED_FIELDS_BY_DOCUMENT_TYPE.getOrDefault(documentType, Set.of());
    }

    public void validateUpdatedFieldFormat(ExtractionFieldEntity field, String value) {
        ValidationErrors errors = new ValidationErrors();
        validateFieldFormat(field.getFieldName(), value, errors);

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public void validateFieldEditAllowed(DocumentEntity document) {
        ValidationErrors errors = new ValidationErrors();
        validateFieldEditStatus(document.getDocumentStatus(), errors);

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public void validateManualFieldRequest(
            DocumentEntity document, String fieldName, String displayName, String value) {
        ValidationErrors errors = new ValidationErrors();

        validateFieldEditStatus(document.getDocumentStatus(), errors);

        String normalizedFieldName = normalizeFieldName(fieldName);

        if (!StringUtils.hasText(normalizedFieldName)) {
            errors.add(
                    "EXTRACTION_FIELD_NAME_INVALID",
                    "Field name must be a known canonical key or use the format custom.<safe_key>.");
        } else if (!isAllowedManualFieldName(normalizedFieldName, document.getDocumentType())) {
            errors.add(
                    "EXTRACTION_FIELD_NAME_INVALID",
                    "Field name must be a known canonical key or use the format custom.<safe_key>.");
        }

        if (normalizedFieldName.startsWith("custom.")
                && !StringUtils.hasText(displayName)) {
            errors.add(
                    "EXTRACTION_FIELD_DISPLAY_NAME_REQUIRED",
                    "Display name is required for custom fields.");
        }

        if (!StringUtils.hasText(value) || !StringUtils.hasText(value.trim())) {
            errors.add("EXTRACTION_FIELD_EMPTY", "Field value cannot be empty.");
        } else {
            validateFieldFormat(normalizedFieldName, value.trim(), errors);
        }

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public boolean isAllowedManualFieldName(String normalizedFieldName, DocumentType documentType) {
        if (CUSTOM_FIELD_NAME_PATTERN.matcher(normalizedFieldName).matches()) {
            return true;
        }

        return ALLOWED_CANONICAL_FIELD_NAMES.contains(normalizedFieldName);
    }

    public Set<String> getAllowedManualFieldNames(DocumentType documentType) {
        Set<String> allowed = new HashSet<>(getRequiredFields(documentType));
        allowed.addAll(ALLOWED_CANONICAL_FIELD_NAMES);

        if (documentType == DocumentType.INVOICE) {
            allowed.addAll(REQUIRED_INVOICE_FIELDS);
        } else if (documentType == DocumentType.RECEIPT) {
            allowed.addAll(REQUIRED_RECEIPT_FIELDS);
            allowed.addAll(RECEIPT_DATE_FIELD_ALIASES);
        } else if (documentType == DocumentType.BANK_STATEMENT) {
            allowed.addAll(REQUIRED_BANK_STATEMENT_FIELDS);
            allowed.addAll(BANK_STATEMENT_IDENTITY_FIELDS);
            allowed.addAll(BANK_STATEMENT_ACTIVITY_FIELDS);
        }

        return allowed.stream().sorted().collect(Collectors.toUnmodifiableSet());
    }

    private void validateFieldEditStatus(DocumentStatus status, ValidationErrors errors) {
        if (status == null || !FIELD_EDIT_ALLOWED_STATUSES.contains(status)) {
            errors.add(
                    "DOCUMENT_STATUS_INVALID",
                    "Extraction fields can only be changed while the document is extracted or"
                            + " awaiting correction.");
        }
    }

    private static Set<String> buildAllowedCanonicalFieldNames() {
        Set<String> names = new HashSet<>();
        names.addAll(REQUIRED_INVOICE_FIELDS);
        names.addAll(REQUIRED_RECEIPT_FIELDS);
        names.addAll(RECEIPT_DATE_FIELD_ALIASES);
        names.addAll(REQUIRED_BANK_STATEMENT_FIELDS);
        names.addAll(BANK_STATEMENT_IDENTITY_FIELDS);
        names.addAll(BANK_STATEMENT_ACTIVITY_FIELDS);
        names.addAll(DATE_FIELDS);
        names.addAll(NUMERIC_FIELDS);
        names.add("line_item");
        names.add("description");
        names.add("approved");
        names.add("applicant");
        names.add("reference");
        names.add("payment_reference");
        return Set.copyOf(names);
    }

    private void validateExtractionFields(ExtractionEntity extraction, ValidationErrors errors) {
        if (extraction == null) {
            errors.add("EXTRACTION_MISSING", "Extraction result was not found.");
            return;
        }

        List<ExtractionFieldEntity> fields = extraction.getFields();

        if (fields == null || fields.isEmpty()) {
            errors.add("EXTRACTION_FIELDS_MISSING", "No extraction fields were found.");
            return;
        }

        DocumentType documentType =
                extraction.getDocument() != null
                        ? extraction.getDocument().getDocumentType()
                        : null;

        validateRequiredFieldsForDocumentType(documentType, fields, errors);

        if (documentType == DocumentType.RECEIPT) {
            validateReceiptBasicStructure(fields, errors);
        }

        if (documentType == DocumentType.BANK_STATEMENT) {
            validateBankStatementBasicStructure(fields, errors);
        }

        validateFieldFormats(fields, errors);
        validateAmountRelationships(fields, errors);
    }

    private void validateAmountRelationships(
            List<ExtractionFieldEntity> fields, ValidationErrors errors) {
        Map<String, ExtractionFieldEntity> fieldsByName =
                fields.stream()
                        .filter(field -> StringUtils.hasText(field.getFieldName()))
                        .collect(
                                Collectors.toMap(
                                        field -> normalizeFieldName(field.getFieldName()),
                                        field -> field,
                                        (first, second) -> first));

        BigDecimal total = parseAmountForRelationship(fieldsByName.get("total_amount"));

        if (total == null) {
            return;
        }

        validateTotalIsNotLessThanComponent(
                total, fieldsByName.get("net_amount"), "net_amount", errors);
        validateTotalIsNotLessThanComponent(
                total, fieldsByName.get("subtotal_amount"), "subtotal_amount", errors);
        validateTotalIsNotLessThanComponent(
                total, fieldsByName.get("vat_amount"), "vat_amount", errors);
        validateTotalIsNotLessThanComponent(
                total, fieldsByName.get("tax_amount"), "tax_amount", errors);
        validateTotalIsNotLessThanComponent(
                total, fieldsByName.get("total_tax_amount"), "total_tax_amount", errors);

        BigDecimal net = parseAmountForRelationship(fieldsByName.get("net_amount"));
        BigDecimal vat = parseAmountForRelationship(fieldsByName.get("vat_amount"));

        if (net != null && vat != null) {
            validateTotalMatchesSum(total, net, vat, "net_amount + vat_amount", errors);
            return;
        }

        BigDecimal subtotal = parseAmountForRelationship(fieldsByName.get("subtotal_amount"));
        BigDecimal totalTax = parseAmountForRelationship(fieldsByName.get("total_tax_amount"));

        if (subtotal != null && totalTax != null) {
            validateTotalMatchesSum(
                    total, subtotal, totalTax, "subtotal_amount + total_tax_amount", errors);
        }
    }

    private void validateTotalIsNotLessThanComponent(
            BigDecimal total,
            ExtractionFieldEntity componentField,
            String componentFieldName,
            ValidationErrors errors) {
        BigDecimal component = parseAmountForRelationship(componentField);

        if (component == null) {
            return;
        }

        if (total.compareTo(component) < 0) {
            errors.add(
                    "EXTRACTION_FIELD_AMOUNT_INCONSISTENT",
                    "total_amount cannot be smaller than " + componentFieldName + ".");
        }
    }

    private void validateTotalMatchesSum(
            BigDecimal total,
            BigDecimal baseAmount,
            BigDecimal taxAmount,
            String formulaLabel,
            ValidationErrors errors) {
        BigDecimal expected = baseAmount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedTotal = total.setScale(2, RoundingMode.HALF_UP);

        if (roundedTotal.subtract(expected).abs().compareTo(AMOUNT_TOTAL_TOLERANCE) > 0) {
            errors.add(
                    "EXTRACTION_FIELD_AMOUNT_INCONSISTENT",
                    "total_amount must match " + formulaLabel + " within 0.01.");
        }
    }

    private BigDecimal parseAmountForRelationship(ExtractionFieldEntity field) {
        if (field == null
                || Boolean.TRUE.equals(field.getPlaceholder())
                || !StringUtils.hasText(field.getValue())) {
            return null;
        }

        String normalized = field.getValue().trim().replace(',', '.');

        try {
            return new BigDecimal(normalized).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void validateRequiredFieldsForDocumentType(
            DocumentType documentType,
            List<ExtractionFieldEntity> fields,
            ValidationErrors errors) {
        Set<String> requiredFields = getRequiredFields(documentType);

        if (requiredFields.isEmpty()) {
            return;
        }

        Map<String, ExtractionFieldEntity> fieldsByName =
                fields.stream()
                        .filter(field -> StringUtils.hasText(field.getFieldName()))
                        .collect(
                                Collectors.toMap(
                                        field -> normalizeFieldName(field.getFieldName()),
                                        field -> field,
                                        (first, second) -> first));

        for (String requiredField : requiredFields) {
            ExtractionFieldEntity field = fieldsByName.get(requiredField);

            if (field == null || Boolean.TRUE.equals(field.getPlaceholder())) {
                errors.add(
                        "EXTRACTION_REQUIRED_FIELD_MISSING",
                        "Required field '"
                                + requiredField
                                + "' is missing and must be filled before confirmation.");
                continue;
            }

            if (!StringUtils.hasText(field.getValue())) {
                errors.add(
                        "EXTRACTION_FIELD_EMPTY", "Field '" + requiredField + "' cannot be empty.");
            }
        }
    }

    private void validateReceiptBasicStructure(
            List<ExtractionFieldEntity> fields, ValidationErrors errors) {
        if (!hasAnyNonBlankField(fields, RECEIPT_DATE_FIELD_ALIASES)) {
            errors.add(
                    "EXTRACTION_REQUIRED_FIELD_MISSING",
                    "Receipt must contain at least one date field: receipt_date, expense_date, transaction_date or purchase_date.");
        }
    }

    private void validateBankStatementBasicStructure(
            List<ExtractionFieldEntity> fields, ValidationErrors errors) {
        if (!hasAnyNonBlankField(fields, BANK_STATEMENT_IDENTITY_FIELDS)) {
            errors.add(
                    "EXTRACTION_REQUIRED_FIELD_MISSING",
                    "Bank statement must contain at least one identity field: bank_name, client_name, account_holder_name or customer_name.");
        }

        if (!hasAnyNonBlankField(fields, BANK_STATEMENT_ACTIVITY_FIELDS)) {
            errors.add(
                    "EXTRACTION_REQUIRED_FIELD_MISSING",
                    "Bank statement must contain at least one statement date, balance or transaction field.");
        }
    }

    private boolean hasAnyNonBlankField(
            List<ExtractionFieldEntity> fields, Set<String> acceptedFieldNames) {
        return fields.stream()
                .filter(field -> StringUtils.hasText(field.getFieldName()))
                .anyMatch(
                        field ->
                                acceptedFieldNames.contains(
                                                normalizeFieldName(field.getFieldName()))
                                        && StringUtils.hasText(field.getValue())
                                        && !Boolean.TRUE.equals(field.getPlaceholder()));
    }

    private void validateLowConfidenceFieldsAreCorrected(
            ExtractionEntity extraction, ValidationErrors errors) {
        if (extraction == null
                || extraction.getFields() == null
                || extraction.getFields().isEmpty()) {
            return;
        }

        DocumentType documentType =
                extraction.getDocument() != null
                        ? extraction.getDocument().getDocumentType()
                        : null;

        for (ExtractionFieldEntity field : extraction.getFields()) {
            if (Boolean.TRUE.equals(field.getPlaceholder())) {
                continue;
            }

            if (!isLowConfidence(field) || Boolean.TRUE.equals(field.getCorrected())) {
                continue;
            }

            if (!shouldLowConfidenceBlockConfirmation(documentType, field)) {
                continue;
            }

            errors.add(
                    "EXTRACTION_FIELD_LOW_CONFIDENCE",
                    "Field '"
                            + field.getFieldName()
                            + "' has confidence below 70% and must be manually reviewed before confirmation.");
        }
    }

    private boolean shouldLowConfidenceBlockConfirmation(
            DocumentType documentType, ExtractionFieldEntity field) {
        String normalizedFieldName = normalizeFieldName(field.getFieldName());

        if (documentType == DocumentType.INVOICE) {
            return true;
        }

        if (documentType == DocumentType.RECEIPT) {
            return REQUIRED_RECEIPT_FIELDS.contains(normalizedFieldName)
                    || RECEIPT_DATE_FIELD_ALIASES.contains(normalizedFieldName);
        }

        if (documentType == DocumentType.BANK_STATEMENT) {
            return REQUIRED_BANK_STATEMENT_FIELDS.contains(normalizedFieldName)
                    || BANK_STATEMENT_IDENTITY_FIELDS.contains(normalizedFieldName)
                    || BANK_STATEMENT_ACTIVITY_FIELDS.contains(normalizedFieldName);
        }

        return false;
    }

    private void validateFieldFormats(List<ExtractionFieldEntity> fields, ValidationErrors errors) {
        for (ExtractionFieldEntity field : fields) {
            if (Boolean.TRUE.equals(field.getPlaceholder())) {
                continue;
            }

            validateFieldFormat(field.getFieldName(), field.getValue(), errors);
        }
    }

    private void validateFieldFormat(String fieldName, String value, ValidationErrors errors) {
        String normalizedFieldName = normalizeFieldName(fieldName);

        if (isDateField(normalizedFieldName)) {
            validateDateFormat(fieldName, value, errors);
            return;
        }

        if (isNumericField(normalizedFieldName)) {
            validateNumericFormat(fieldName, value, errors);
        }
    }

    private void validateDateFormat(String fieldName, String value, ValidationErrors errors) {
        if (!StringUtils.hasText(value)) {
            return;
        }

        String trimmed = value.trim();

        for (DateTimeFormatter formatter : ACCEPTED_DATE_FORMATS) {
            try {
                LocalDate.parse(trimmed, formatter);
                return;
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        errors.add(
                "EXTRACTION_FIELD_DATE_FORMAT_INVALID",
                "Field '"
                        + fieldName
                        + "' must be a valid European date. Supported formats are DD.MM.YYYY or DD/MM/YYYY. Ambiguous US format MM/DD/YYYY is not supported.");
    }

    private void validateNumericFormat(String fieldName, String value, ValidationErrors errors) {
        if (!StringUtils.hasText(value)) {
            return;
        }

        String trimmed = value.trim();

        if (trimmed.contains(" ") || trimmed.contains("\t")) {
            addNumericFormatError(fieldName, errors);
            return;
        }

        if (trimmed.contains(",") && trimmed.contains(".")) {
            addNumericFormatError(fieldName, errors);
            return;
        }

        int comma = trimmed.indexOf(',');
        int dot = trimmed.indexOf('.');

        if (comma >= 0 && trimmed.indexOf(',', comma + 1) >= 0) {
            addNumericFormatError(fieldName, errors);
            return;
        }

        if (dot >= 0 && trimmed.indexOf('.', dot + 1) >= 0) {
            addNumericFormatError(fieldName, errors);
            return;
        }

        String normalized = trimmed.replace(',', '.');

        try {
            BigDecimal amount = new BigDecimal(normalized);

            if (amount.signum() < 0 || amount.scale() > 2) {
                addNumericFormatError(fieldName, errors);
            }
        } catch (NumberFormatException exception) {
            addNumericFormatError(fieldName, errors);
        }
    }

    private void addNumericFormatError(String fieldName, ValidationErrors errors) {
        errors.add(
                "EXTRACTION_FIELD_NUMERIC_FORMAT_INVALID",
                "Field '"
                        + fieldName
                        + "' must be a numeric value only, without currency symbols or additional text. Use for example 1500, 1500.50 or 1500,50.");
    }

    private boolean isDateField(String normalizedFieldName) {
        return DATE_FIELDS.contains(normalizedFieldName)
                || normalizedFieldName.contains("date")
                || normalizedFieldName.contains("datum");
    }

    private boolean isNumericField(String normalizedFieldName) {
        return NUMERIC_FIELDS.contains(normalizedFieldName)
                || normalizedFieldName.contains("amount")
                || normalizedFieldName.contains("balance")
                || normalizedFieldName.contains("deposit")
                || normalizedFieldName.contains("withdrawal")
                || normalizedFieldName.contains("iznos")
                || normalizedFieldName.contains("cijena")
                || normalizedFieldName.endsWith("_price")
                || normalizedFieldName.endsWith("_quantity");
    }

    private boolean isLowConfidence(ExtractionFieldEntity field) {
        return field.getConfidence() != null
                && field.getConfidence().compareTo(MIN_CONFIDENCE_FOR_AUTO_CONFIRM) < 0;
    }

    private String normalizeFieldName(String fieldName) {
        if (fieldName == null) {
            return "";
        }

        return fieldName.trim().toLowerCase(Locale.ROOT);
    }
}
