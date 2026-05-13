package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExtractionValidation {

    private static final BigDecimal MIN_CONFIDENCE_FOR_AUTO_CONFIRM = new BigDecimal("0.70");

    private static final Set<String> REQUIRED_INVOICE_FIELDS =
            Set.of("invoice_id", "invoice_date", "supplier_name", "total_amount", "currency");

    private static final Set<String> DATE_FIELDS =
            Set.of("invoice_date", "due_date", "delivery_date", "issue_date", "payment_due_date");

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
                    "qty");

    private static final List<DateTimeFormatter> ACCEPTED_DATE_FORMATS =
            List.of(
                    DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT),
                    DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT),
                    DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT));

    public void validateRequiredFields(ExtractionEntity extraction) {
        ValidationErrors errors = new ValidationErrors();

        validateExtractionFields(extraction, errors);
        validateLowConfidenceFieldsAreCorrected(extraction, errors);

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public void validateUpdatedFieldFormat(ExtractionFieldEntity field, String value) {
        ValidationErrors errors = new ValidationErrors();
        validateFieldFormat(field.getFieldName(), value, errors);
        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    private void validateLowConfidenceFieldsAreCorrected(
            ExtractionEntity extraction, ValidationErrors errors) {
        for (ExtractionFieldEntity field : extraction.getFields()) {
            if (isLowConfidence(field) && !Boolean.TRUE.equals(field.getCorrected())) {
                errors.add(
                        "EXTRACTION_FIELD_LOW_CONFIDENCE",
                        "Field '"
                                + field.getFieldName()
                                + "' has confidence below 70% and must be manually reviewed before confirmation.");
            }
        }
    }

    private void validateExtractionFields(ExtractionEntity extraction, ValidationErrors errors) {
        List<ExtractionFieldEntity> fields = extraction.getFields();

        if (fields == null || fields.isEmpty()) {
            errors.add("EXTRACTION_FIELDS_MISSING", "No extraction fields were found.");
            return;
        }

        if (extraction.getDocument() != null
                && extraction.getDocument().getDocumentType() == DocumentType.INVOICE) {
            validateRequiredInvoiceFields(fields, errors);
        }

        validateFieldsAreNotBlank(fields, errors);
        validateFieldFormats(fields, errors);
    }

    private void validateRequiredInvoiceFields(
            List<ExtractionFieldEntity> fields, ValidationErrors errors) {
        Set<String> extractedFieldNames =
                fields.stream()
                        .map(ExtractionFieldEntity::getFieldName)
                        .filter(StringUtils::hasText)
                        .map(this::normalizeFieldName)
                        .collect(Collectors.toSet());

        for (String requiredField : REQUIRED_INVOICE_FIELDS) {
            if (!extractedFieldNames.contains(requiredField)) {
                errors.add(
                        "EXTRACTION_REQUIRED_FIELD_MISSING",
                        "Required field '" + requiredField + "' is missing.");
            }
        }
    }

    private void validateFieldsAreNotBlank(
            List<ExtractionFieldEntity> fields, ValidationErrors errors) {
        for (ExtractionFieldEntity field : fields) {
            if (!StringUtils.hasText(field.getValue())) {
                errors.add(
                        "EXTRACTION_FIELD_EMPTY",
                        "Field '" + field.getFieldName() + "' cannot be empty.");
            }
        }
    }

    private void validateFieldFormats(List<ExtractionFieldEntity> fields, ValidationErrors errors) {
        for (ExtractionFieldEntity field : fields) {
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
                        + "' must be a valid date in YYYY-MM-DD, DD.MM.YYYY or DD/MM/YYYY format.");
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
                        + "' must be a non-negative number with at most 2 decimal places.");
    }

    private boolean isDateField(String normalizedFieldName) {
        return DATE_FIELDS.contains(normalizedFieldName)
                || normalizedFieldName.contains("date")
                || normalizedFieldName.contains("datum");
    }

    private boolean isNumericField(String normalizedFieldName) {
        return NUMERIC_FIELDS.contains(normalizedFieldName)
                || normalizedFieldName.contains("amount")
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
        return fieldName.trim().toLowerCase(Locale.ROOT);
    }
}
