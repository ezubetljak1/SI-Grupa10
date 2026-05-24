package ba.unsa.si.docflow.service.security;

import ba.unsa.si.docflow.dao.TaskDAO;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.TaskEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.entity.enums.TaskType;
import ba.unsa.si.docflow.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowPermissionServiceImpl implements WorkflowPermissionService {

    private final CurrentUserService currentUserService;
    private final TaskDAO taskDAO;

    @Override
    public void requireCanViewAudit(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        if (!canViewAudit()) {

            throwForbidden("You do not have permission to view audit logs.");
        }
    }

    @Override
    public void requireCanAssignTask(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        if (role != RoleName.ADMIN && role != RoleName.MANAGER) {
            throwForbidden("You do not have permission to assign tasks.");
        }
    }

    @Override
    public void requireCanViewNotifications() {
        // ne mogu se vidjeti tudje notifikacije, implementira se kasnije kroz notification
        // endpointe
    }

    @Override
    public void requireCanApprove(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        if (role != RoleName.ADMIN && role != RoleName.MANAGER && role != RoleName.APPROVER) {

            throwForbidden("You do not have permission to approve documents.");
        }

        requireAssignedTaskIfPresent(document, role, TaskType.APPROVAL);
    }

    @Override
    public void requireCanEditExtraction(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        if (role == RoleName.APPROVER) {

            throwForbidden("Approvers cannot edit extraction fields.");
        }

        requireAssignedTaskIfPresent(document, role, TaskType.CORRECTION);
    }

    @Override
    public void requireCanRunExtraction(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());
        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        requireAssignedTaskIfPresent(document, role, TaskType.EXTRACTION);
    }

    @Override
    public void requireCanConfirmExtraction(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        if (role == RoleName.APPROVER) {

            throwForbidden("Approvers cannot confirm extraction.");
        }

        TaskType taskType =
                document.getDocumentStatus() == DocumentStatus.NEEDS_CORRECTION
                        ? TaskType.CORRECTION
                        : TaskType.EXTRACTION;

        requireAssignedTaskIfPresent(document, role, taskType);
    }

    @Override
    public void requireSameCompany(Long companyId) {

        Long currentCompanyId = currentUserService.getCurrentUser().companyId();

        if (!currentCompanyId.equals(companyId)) {

            throwForbidden("Cross-company access is forbidden.");
        }
    }

    @Override
    public boolean canViewAudit() {

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        return role == RoleName.ADMIN || role == RoleName.MANAGER;
    }

    @Override
    public boolean canApprove() {

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        return role == RoleName.ADMIN || role == RoleName.MANAGER || role == RoleName.APPROVER;
    }

    @Override
    public boolean canManageExtraction() {

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        return role != RoleName.APPROVER;
    }

    @Override
    public boolean canViewAllTasks() {

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        return role == RoleName.ADMIN || role == RoleName.MANAGER;
    }

    @Override
    public boolean canAddManualField() {

        RoleName role = RoleName.valueOf(currentUserService.getCurrentUser().role());

        return role != RoleName.APPROVER;
    }

    private void throwForbidden(String message) {
        throw new AccessDeniedException(message);
    }

    private void requireAssignedTaskIfPresent(
            DocumentEntity document, RoleName role, TaskType taskType) {
        if (role == RoleName.ADMIN || role == RoleName.MANAGER) {
            return;
        }

        TaskEntity activeTask =
                taskDAO.findActiveByDocumentIdAndTaskType(document.getId(), taskType);

        if (activeTask == null) {
            return;
        }

        Long currentUserId = currentUserService.getCurrentUser().userId();
        if (!activeTask.getAssignedUserId().equals(currentUserId)) {
            throwForbidden("This document is assigned to another user.");
        }
    }
}
