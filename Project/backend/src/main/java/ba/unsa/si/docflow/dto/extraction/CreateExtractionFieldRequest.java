package ba.unsa.si.docflow.dto.extraction;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateExtractionFieldRequest {

    @NotBlank(message = "Field name is required.")
    private String fieldName;

    private String displayName;

    private String value;
}
