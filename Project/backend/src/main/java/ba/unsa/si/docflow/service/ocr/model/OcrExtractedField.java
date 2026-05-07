package ba.unsa.si.docflow.service.ocr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OcrExtractedField {

    private String type;
    private String value;
    private String normalizedValue;
    private BigDecimal confidence;
}
