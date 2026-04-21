package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.dto.document.Document;
import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;

public interface DocumentService {
    PagedResponse<Document> find(DocumentFilterRequest request);
    ApiResponse<Document> findById(Long id);
    ApiResponse<Document> create(DocumentCreateRequest request);
    ApiResponse<Document> update(DocumentUpdateRequest request);
    ApiResponse<String> delete(Long id);
}