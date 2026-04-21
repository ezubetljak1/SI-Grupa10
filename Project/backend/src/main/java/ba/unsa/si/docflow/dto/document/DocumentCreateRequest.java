package ba.unsa.si.docflow.dto.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DocumentCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long companyId;

    @NotNull
    private Long createdByUserId;

    @NotBlank
    private String name;

    @NotBlank
    private String fileType;

    @NotBlank
    private String documentType;

    @NotBlank
    private String storagePath;

    @NotNull
    private Long fileSize;
}