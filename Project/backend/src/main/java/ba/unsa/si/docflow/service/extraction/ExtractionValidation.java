package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExtractionValidation {
    private static final BigDecimal MIN_CONFIDENCE_FOR_AUTO_CONFIRM = new BigDecimal("0.70");

    public void validateRequiredFields(ExtractionEntity extraction) {
        ValidationErrors errors = new ValidationErrors();

        validateLowConfidenceFieldsAreCorrected(extraction, errors);
        validateRequiredFieldsExist(extraction, errors);

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

    private void validateRequiredFieldsExist(
            ExtractionEntity extraction,
            ValidationErrors errors
    ) {

        for (ExtractionFieldEntity field : extraction.getFields()) {

            String fieldName = field.getFieldName();
            String value = field.getValue();

            if (isRequiredField(fieldName)
                    && !StringUtils.hasText(value)) {

                errors.add(
                        "EXTRACTION_REQUIRED_FIELD_MISSING",
                        "Field '" + fieldName + "' is required and cannot be empty."
                );
            }
        }
    }

    private boolean isLowConfidence(ExtractionFieldEntity field) {
        return field.getConfidence() != null
                && field.getConfidence().compareTo(MIN_CONFIDENCE_FOR_AUTO_CONFIRM) < 0;
    }

    private boolean isRequiredField(String fieldName) {

        String normalized = fieldName.toLowerCase();

        return normalized.equals("invoice_id")
                || normalized.contains("amount")
                || normalized.contains("date")
                || normalized.contains("currency");
    }
}
