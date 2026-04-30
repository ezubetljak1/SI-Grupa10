package ba.unsa.si.docflow.exception;

import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.ValidationErrors;

import lombok.AllArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("STORAGE_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBeanValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("INTERNAL_SERVER_ERROR", ex.getMessage()));
    }
}
