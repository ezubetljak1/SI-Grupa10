package ba.unsa.si.docflow.dto.extraction;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExtractionResponse {

    private Long id;
    private Long documentId;
    private String rawJson;
    private LocalDateTime extractionTime;
    private List<ExtractionFieldResponse> fields;
}
