package ba.unsa.si.docflow.entity.enums;

public enum DocumentStatus {
    UPLOADED,
    PROCESSING_FAILED,
    EXTRACTED,
    UNDER_REVIEW,
    NEEDS_CLASSIFICATION_REVIEW,
    READY_FOR_APPROVAL,
    APPROVED,
    REJECTED,
    COMPLETED
}
