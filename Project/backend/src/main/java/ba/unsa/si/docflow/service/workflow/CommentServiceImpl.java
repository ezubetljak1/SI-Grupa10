package ba.unsa.si.docflow.service.workflow;

import ba.unsa.si.docflow.dao.CommentDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.workflow.CommentResponse;
import ba.unsa.si.docflow.dto.workflow.CreateCommentRequest;
import ba.unsa.si.docflow.entity.CommentEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.CommentType;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.mapper.CommentMapper;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.security.CurrentUserService;
import ba.unsa.si.docflow.service.document.DocumentValidation;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;
    private final CommentMapper commentMapper;
    private final DocumentValidation documentValidation;
    private final CurrentUserService currentUserService;
    private final UserDAO userDAO;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CommentResponse>> getComments(Long documentId) {
        currentUserService.requireAnyRole(
                RoleName.ADMIN, RoleName.OPERATOR, RoleName.APPROVER, RoleName.MANAGER);
        DocumentEntity document =
                documentValidation.validateExistsInCompany(
                        documentId, currentUserService.getCurrentCompanyId());

        List<CommentEntity> comments = commentDAO.findByDocumentIdOrderByCreatedAt(document.getId());
        Map<Long, String> userNames = resolveUserNames(comments, document.getCompanyId());

        return new ApiResponse<>(
                "OK",
                commentMapper.entitiesToDtos(
                        comments, userId -> userNames.getOrDefault(userId, "Unknown user")));
    }

    @Override
    public ApiResponse<CommentResponse> createGeneralComment(
            Long documentId, CreateCommentRequest request) {
        currentUserService.requireAnyRole(
                RoleName.ADMIN, RoleName.OPERATOR, RoleName.APPROVER, RoleName.MANAGER);
        DocumentEntity document =
                documentValidation.validateExistsInCompany(
                        documentId, currentUserService.getCurrentCompanyId());

        validateRequiredComment(request.getContent());

        CommentEntity saved =
                createTypedComment(
                        document,
                        currentUserService.getCurrentUserId(),
                        CommentType.GENERAL,
                        request.getContent().trim());

        Map<Long, String> userNames =
                resolveUserNames(List.of(saved), document.getCompanyId());

        return new ApiResponse<>(
                "OK",
                commentMapper.entityToDto(
                        saved,
                        userId -> userNames.getOrDefault(userId, "Unknown user")));
    }

    @Override
    public CommentEntity createTypedComment(
            DocumentEntity document, Long userId, CommentType type, String content) {
        validateRequiredComment(content);

        CommentEntity comment = new CommentEntity();
        comment.setDocument(document);
        comment.setUserId(userId);
        comment.setType(type);
        comment.setContent(content.trim());

        return commentDAO.persist(comment);
    }

    @Override
    public void validateRequiredComment(String content) {
        if (!StringUtils.hasText(content) || content.trim().isEmpty()) {
            ValidationErrors errors = new ValidationErrors();
            errors.add("COMMENT_REQUIRED", "Comment is required for this action.");
            throw new ApiValidationException(errors);
        }
    }

    private Map<Long, String> resolveUserNames(List<CommentEntity> comments, Long companyId) {
        Map<Long, String> names = new HashMap<>();

        for (CommentEntity comment : comments) {
            Long userId = comment.getUserId();
            if (userId == null || names.containsKey(userId)) {
                continue;
            }

            UserEntity user = userDAO.findByIdAndCompanyId(userId, companyId);
            names.put(userId, formatUserName(user, userId));
        }

        return names;
    }

    private String formatUserName(UserEntity user, Long userId) {
        if (user == null) {
            return "User #" + userId;
        }

        String firstName = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String lastName = user.getLastName() != null ? user.getLastName().trim() : "";
        String fullName = (firstName + " " + lastName).trim();

        if (StringUtils.hasText(fullName)) {
            return fullName;
        }

        if (StringUtils.hasText(user.getEmail())) {
            return user.getEmail();
        }

        return "User #" + userId;
    }
}
