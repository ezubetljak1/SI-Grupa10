package ba.unsa.si.docflow.dto.extraction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExtractionFieldResponse {

    private Long id;
    private String fieldName;
    private String value;
    private BigDecimal confidence;
    private Boolean corrected;
    private Boolean placeholder;
    private String displayName;
    private Boolean manual;
}
