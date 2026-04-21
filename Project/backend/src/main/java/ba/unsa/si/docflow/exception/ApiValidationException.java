package ba.unsa.si.docflow.exception;

import ba.unsa.si.docflow.response.ValidationErrors;
import lombok.Getter;

@Getter
public class ApiValidationException extends RuntimeException {
    private final ValidationErrors validationErrors;

    public ApiValidationException(ValidationErrors validationErrors){
        super("Validation failed");
        this.validationErrors = validationErrors;
    }
}
