package ba.unsa.si.docflow.service.workflow;

import ba.unsa.si.docflow.dto.workflow.CommentResponse;
import ba.unsa.si.docflow.dto.workflow.CreateCommentRequest;
import ba.unsa.si.docflow.entity.CommentEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.CommentType;
import ba.unsa.si.docflow.response.ApiResponse;

import java.util.List;

public interface CommentService {

    ApiResponse<List<CommentResponse>> getComments(Long documentId);

    ApiResponse<CommentResponse> createGeneralComment(Long documentId, CreateCommentRequest request);

    CommentEntity createTypedComment(
            DocumentEntity document,
            Long userId,
            CommentType type,
            String content);

    void validateRequiredComment(String content);
}
