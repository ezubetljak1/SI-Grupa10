package ba.unsa.si.docflow.service.workflow;

import ba.unsa.si.docflow.dao.StatusHistoryDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.workflow.StatusHistoryResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.StatusHistoryEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.mapper.StatusHistoryMapper;
import ba.unsa.si.docflow.response.ApiResponse;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatusHistoryServiceImpl implements StatusHistoryService {

    private final StatusHistoryDAO statusHistoryDAO;
    private final StatusHistoryMapper statusHistoryMapper;
    private final DocumentValidation documentValidation;
    private final CurrentUserService currentUserService;
    private final UserDAO userDAO;

    @Override
    public ApiResponse<List<StatusHistoryResponse>> getStatusHistory(Long documentId) {
        currentUserService.requireAnyRole(
                RoleName.ADMIN, RoleName.OPERATOR, RoleName.APPROVER, RoleName.MANAGER);
        DocumentEntity document =
                documentValidation.validateExistsInCompany(
                        documentId, currentUserService.getCurrentCompanyId());

        List<StatusHistoryEntity> history =
                statusHistoryDAO.findByDocumentIdOrderByChangedAt(document.getId());
        Map<Long, String> userNames = resolveUserNames(history, document.getCompanyId());

        return new ApiResponse<>(
                "OK",
                statusHistoryMapper.entitiesToDtos(
                        history, userId -> userNames.getOrDefault(userId, "Unknown user")));
    }

    private Map<Long, String> resolveUserNames(
            List<StatusHistoryEntity> historyEntries, Long companyId) {
        Map<Long, String> names = new HashMap<>();

        for (StatusHistoryEntity entry : historyEntries) {
            Long userId = entry.getChangedByUserId();
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
