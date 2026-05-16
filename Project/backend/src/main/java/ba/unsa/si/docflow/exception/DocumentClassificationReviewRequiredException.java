package ba.unsa.si.docflow.exception;

import ba.unsa.si.docflow.entity.enums.DocumentType;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DocumentClassificationReviewRequiredException extends ExtractionException {
    private final Long documentId;
    private final DocumentType detectedDocumentType;
    private final BigDecimal classificationConfidence;

    public DocumentClassificationReviewRequiredException(
            Long documentId,
            DocumentType detectedDocumentType,
            BigDecimal classificationConfidence) {
        super("Document classification requires manual review.");
        this.documentId = documentId;
        this.detectedDocumentType = detectedDocumentType;
        this.classificationConfidence = classificationConfidence;
    }
}
