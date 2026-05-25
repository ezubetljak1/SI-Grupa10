package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.dto.document.*;
import ba.unsa.si.docflow.dto.workflow.CommentResponse;
import ba.unsa.si.docflow.dto.workflow.CreateCommentRequest;
import ba.unsa.si.docflow.dto.workflow.StatusHistoryResponse;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    PagedResponse<Document> find(DocumentFilterRequest request);

    ApiResponse<Document> findById(Long id);

    ApiResponse<Document> create(DocumentCreateRequest request);

    ApiResponse<Document> update(DocumentUpdateRequest request);

    ApiResponse<String> delete(Long id);

    ApiResponse<Document> upload(MultipartFile file, String documentType, String name);

    ApiResponse<Document> confirmDocumentType(Long id, ConfirmDocumentTypeRequest request);

    ApiResponse<Document> approveDocument(Long id, CreateCommentRequest request);

    ApiResponse<Document> rejectDocument(Long id, CreateCommentRequest request);

    ApiResponse<Document> returnDocumentForCorrection(Long id, CreateCommentRequest request);

    DocumentFileResponse downloadFile(Long id);

    ApiResponse<List<StatusHistoryResponse>> getStatusHistory(Long id);

    ApiResponse<List<CommentResponse>> getComments(Long id);

    ApiResponse<CommentResponse> createComment(Long id, CreateCommentRequest request);
}
