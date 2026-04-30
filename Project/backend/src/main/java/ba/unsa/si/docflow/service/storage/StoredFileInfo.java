package ba.unsa.si.docflow.service.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoredFileInfo {
    private String originalFileName;
    private String storedFileName;
    private String relativePath;
    private Long size;
    private String contentType;
    private String extension;
}
