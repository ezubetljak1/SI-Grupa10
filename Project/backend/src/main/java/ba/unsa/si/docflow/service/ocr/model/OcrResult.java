package ba.unsa.si.docflow.service.ocr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OcrResult {

    private String rawText;
    private List<OcrExtractedField> fields;
}
