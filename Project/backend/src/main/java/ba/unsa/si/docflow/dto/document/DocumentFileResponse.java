package ba.unsa.si.docflow.dto.document;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
public class DocumentFileResponse {
    private Resource resource;
    private String fileName;
    private String contentType;
}
