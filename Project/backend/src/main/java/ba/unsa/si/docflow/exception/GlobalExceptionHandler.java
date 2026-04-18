package ba.unsa.si.docflow.exception;

import ba.unsa.si.docflow.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<?> handleValidation(ApiValidationException ex){
        return ResponseEntity.badRequest().body(ex.getValidationErrors().getErrors());
    }

    @ExceptionHandler(ApiNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(ApiNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBeanValidation(MethodArgumentNotValidException ex){
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("INTERNAL_SERVER_ERROR", ex.getMessage()));
    }
}
