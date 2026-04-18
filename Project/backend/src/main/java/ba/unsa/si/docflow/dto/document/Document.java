package ba.unsa.si.docflow.dto.document;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Document implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long createdBy;
    private String name;
    private String fileType;
    private String documentType;
    private String storagePath;
    private LocalDateTime uploadDate;
    private Long fileSize;
    private String documentStatus;
}
