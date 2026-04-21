package ba.unsa.si.docflow.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationError {
    private String code;
    private String message;
}
