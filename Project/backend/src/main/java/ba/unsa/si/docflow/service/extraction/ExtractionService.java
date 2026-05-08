package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.dto.extraction.UpdateExtractionFieldRequest;
import ba.unsa.si.docflow.response.ApiResponse;

public interface ExtractionService {

    ApiResponse process(Long documentId);

    ApiResponse retry(Long documentId);

    ApiResponse findByDocumentId(Long documentId);

    ApiResponse findFieldsByDocumentId(Long documentId);

    ApiResponse findFieldsByExtractionId(Long extractionId);

    ApiResponse updateField(Long extractionId, Long fieldId, UpdateExtractionFieldRequest request);

    ApiResponse confirmExtraction(Long documentId);
}
