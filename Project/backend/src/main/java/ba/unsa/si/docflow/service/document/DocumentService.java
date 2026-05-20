package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.dto.document.*;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    PagedResponse<Document> find(DocumentFilterRequest request);

    ApiResponse<Document> findById(Long id);

    ApiResponse<Document> create(DocumentCreateRequest request);

    ApiResponse<Document> update(DocumentUpdateRequest request);

    ApiResponse<String> delete(Long id);

    ApiResponse<Document> upload(MultipartFile file, String documentType, String name);

    ApiResponse<Document> confirmDocumentType(Long id, ConfirmDocumentTypeRequest request);

    DocumentFileResponse downloadFile(Long id);
}
