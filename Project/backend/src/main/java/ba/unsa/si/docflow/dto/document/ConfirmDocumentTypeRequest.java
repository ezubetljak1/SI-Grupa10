package ba.unsa.si.docflow.dto.document;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ConfirmDocumentTypeRequest implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @NotBlank private String documentType;
}
