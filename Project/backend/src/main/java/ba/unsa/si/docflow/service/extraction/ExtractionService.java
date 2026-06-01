package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.dto.extraction.CreateExtractionFieldRequest;
import ba.unsa.si.docflow.dto.extraction.ExtractionFieldResponse;
import ba.unsa.si.docflow.dto.extraction.ExtractionResponse;
import ba.unsa.si.docflow.dto.extraction.UpdateExtractionFieldRequest;
import ba.unsa.si.docflow.response.ApiResponse;

import java.util.List;

public interface ExtractionService {

    ApiResponse<ExtractionResponse> process(Long documentId);

    ApiResponse<ExtractionResponse> retry(Long documentId);

    ApiResponse<ExtractionResponse> findByDocumentId(Long documentId);

    ApiResponse<List<ExtractionFieldResponse>> findFieldsByDocumentId(Long documentId);

    ApiResponse<List<ExtractionFieldResponse>> findFieldsByExtractionId(Long extractionId);

    ApiResponse<ExtractionFieldResponse> updateField(
            Long extractionId, Long fieldId, UpdateExtractionFieldRequest request);

    ApiResponse<ExtractionFieldResponse> addField(
            Long extractionId, CreateExtractionFieldRequest request);

    ApiResponse<ExtractionFieldResponse> deleteField(Long extractionId, Long fieldId);

    ApiResponse<ExtractionResponse> confirmExtraction(Long documentId);
}
