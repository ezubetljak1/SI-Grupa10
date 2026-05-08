package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.entity.ExtractionEntity;

import org.springframework.stereotype.Service;

@Service
public class ExtractionValidation {

    public void validateRequiredFields(ExtractionEntity extraction) {
        // TODO: Implement required extraction field validation as part of a separate Sprint 7 task.
        // This method should validate that all required fields exist and have acceptable values
        // before the extraction can be confirmed.
        // (najbolje gledati po tipu dokumenta koja polja su obavezna)
    }
}
