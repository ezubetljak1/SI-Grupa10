package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExtractionValidation {

    private static final BigDecimal MIN_CONFIDENCE_FOR_AUTO_CONFIRM = new BigDecimal("0.70");

    private static final Set<String> REQUIRED_INVOICE_FIELDS =
            Set.of("invoice_id", "invoice_date", "supplier_name", "total_amount", "currency");

    public void validateRequiredFields(ExtractionEntity extraction) {
        ValidationErrors errors = new ValidationErrors();

        validateExtractionFields(extraction, errors);
        validateLowConfidenceFieldsAreCorrected(extraction, errors);

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

    private boolean isLowConfidence(ExtractionFieldEntity field) {
        return field.getConfidence() != null
                && field.getConfidence().compareTo(MIN_CONFIDENCE_FOR_AUTO_CONFIRM) < 0;
    }

    private String normalizeFieldName(String fieldName) {
        return fieldName.trim().toLowerCase(Locale.ROOT);
    }
}
