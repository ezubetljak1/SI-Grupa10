package ba.unsa.si.docflow.service.ocr;

import ba.unsa.si.docflow.service.ocr.model.OcrResult;

public interface OcrProvider {

    OcrResult process(byte[] fileContent, String mimeType, String processorId);
}
