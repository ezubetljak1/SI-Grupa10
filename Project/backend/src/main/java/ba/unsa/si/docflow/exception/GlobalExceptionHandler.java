package ba.unsa.si.docflow.exception;

import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.ValidationErrors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<?> handleValidation(ApiValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getValidationErrors().getErrors());
    }

    @ExceptionHandler(ApiNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(ApiNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ValidationErrors errors = new ValidationErrors();

        errors.add(
                "DOCUMENT_FILE_SIZE_EXCEEDED",
                messageSource.getMessage(
                        "document.validation.file.size.exceeded", null, Locale.getDefault()));

        return ResponseEntity.badRequest().body(errors.getErrors());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<?> handleMultipartException(MultipartException ex) {
        ValidationErrors errors = new ValidationErrors();

        errors.add(
                "DOCUMENT_FILE_SIZE_EXCEEDED",
                messageSource.getMessage(
                        "document.validation.file.size.exceeded", null, Locale.getDefault()));

        return ResponseEntity.badRequest().body(errors.getErrors());
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiResponse<String>> handleStorage(StorageException ex) {
        log.error("Storage error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("STORAGE_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(KeycloakIntegrationException.class)
    public ResponseEntity<ApiResponse<String>> handleKeycloak(KeycloakIntegrationException ex) {
        log.error("Keycloak integration error occurred", ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiResponse<>("KEYCLOAK_INTEGRATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBeanValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        log.warn("Data integrity violation occurred", ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ApiResponse<>(
                                "DATA_INTEGRITY_VIOLATION",
                                "The requested operation cannot be completed because this record is still referenced by related data."));
    }

    @ExceptionHandler(ExtractionException.class)
    public ResponseEntity<ApiResponse<String>> handleExtraction(ExtractionException ex) {
        log.error("Extraction error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("EXTRACTION_FAILED", ex.getMessage()));
    }

    @ExceptionHandler(DocumentClassificationReviewRequiredException.class)
    public ResponseEntity<ApiResponse<String>> handleDocumentClassificationReviewRequired(
            DocumentClassificationReviewRequiredException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ApiResponse<>(
                                "DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED", exception.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("NOT_FOUND", "Resource not found: " + ex.getResourcePath()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
        log.error("Unexpected application error", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ApiResponse<>(
                                "INTERNAL_SERVER_ERROR",
                                "An unexpected error occurred. Please try again later."));
    }
}
