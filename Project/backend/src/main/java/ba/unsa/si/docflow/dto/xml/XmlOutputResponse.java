package ba.unsa.si.docflow.dto.xml;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class XmlOutputResponse {

    private Long id;

    private Long documentId;

    private String fileName;

    private LocalDateTime generatedAt;

    private Long generatedBy;

    private String content;
}
