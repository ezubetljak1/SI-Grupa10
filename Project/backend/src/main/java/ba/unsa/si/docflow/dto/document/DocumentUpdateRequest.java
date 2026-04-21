package ba.unsa.si.docflow.dto.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DocumentUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Long id;

    private String name;
    private String documentType;
    private String documentStatus;
}
