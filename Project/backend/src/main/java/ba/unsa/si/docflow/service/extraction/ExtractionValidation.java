package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExtractionValidation {
    private static final BigDecimal MIN_CONFIDENCE_FOR_AUTO_CONFIRM = new BigDecimal("0.70");

    public void validateRequiredFields(ExtractionEntity extraction) {
        ValidationErrors errors = new ValidationErrors();

        validateLowConfidenceFieldsAreCorrected(extraction, errors);
        // TODO: Implement required extraction field validation as part of a separate Sprint 7 task.
        // This method should validate that all required fields exist and have acceptable values
        // before the extraction can be confirmed.
        // (najbolje gledati po tipu dokumenta koja polja su obavezna)

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

    private boolean isLowConfidence(ExtractionFieldEntity field) {
        return field.getConfidence() != null
                && field.getConfidence().compareTo(MIN_CONFIDENCE_FOR_AUTO_CONFIRM) < 0;
    }
}
