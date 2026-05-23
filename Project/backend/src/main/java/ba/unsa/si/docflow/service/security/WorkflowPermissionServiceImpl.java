package ba.unsa.si.docflow.service.security;

import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowPermissionServiceImpl
        implements WorkflowPermissionService {

    private final CurrentUserService currentUserService;

    @Override
    public void requireCanViewAudit(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        if (!canViewAudit()) {

            throwForbidden(
                    "You do not have permission to view audit logs."
            );
        }
    }

    @Override
    public void requireCanAssignTask(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(
                currentUserService.getCurrentUser().role()
        );

        if (role != RoleName.ADMIN && role != RoleName.MANAGER) {
            throwForbidden(
                    "You do not have permission to assign tasks."
            );
        }
    }

    @Override
    public void requireCanViewNotifications() {

        RoleName role = RoleName.valueOf(
                currentUserService.getCurrentUser().role()
        );

        if (role == RoleName.APPROVER) {
            throwForbidden(
                    "You do not have permission to view notifications."
            );
        }
    }

    @Override
    public void requireCanApprove(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(currentUserService
                .getCurrentUser()
                .role());

        if (role != RoleName.ADMIN
                && role != RoleName.MANAGER
                && role != RoleName.APPROVER) {

            throwForbidden(
                    "You do not have permission to approve documents."
            );
        }
    }

    @Override
    public void requireCanEditExtraction(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(currentUserService
                .getCurrentUser()
                .role());

        if (role == RoleName.APPROVER) {

            throwForbidden(
                    "Approvers cannot edit extraction fields."
            );
        }
    }

    @Override
    public void requireCanRunExtraction(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());
    }

    @Override
    public void requireCanConfirmExtraction(DocumentEntity document) {

        requireSameCompany(document.getCompanyId());

        RoleName role = RoleName.valueOf(currentUserService
                .getCurrentUser()
                .role());

        if (role == RoleName.APPROVER) {

            throwForbidden(
                    "Approvers cannot confirm extraction."
            );
        }
    }

    @Override
    public void requireSameCompany(Long companyId) {

        Long currentCompanyId =
                currentUserService
                        .getCurrentUser()
                        .companyId();

        if (!currentCompanyId.equals(companyId)) {

            throwForbidden(
                    "Cross-company access is forbidden."
            );
        }
    }

    @Override
    public boolean canViewAudit() {

        RoleName role = RoleName.valueOf(currentUserService
                .getCurrentUser()
                .role());

        return role == RoleName.ADMIN
                || role == RoleName.MANAGER;
    }

    @Override
    public boolean canApprove() {

        RoleName role = RoleName.valueOf(currentUserService
                .getCurrentUser()
                .role());

        return role == RoleName.ADMIN
                || role == RoleName.MANAGER
                || role == RoleName.APPROVER;
    }

    @Override
    public boolean canManageExtraction() {

        RoleName role = RoleName.valueOf(currentUserService
                .getCurrentUser()
                .role());

        return role != RoleName.APPROVER;
    }

    @Override
    public boolean canViewAllTasks() {

        RoleName role = RoleName.valueOf(
                currentUserService.getCurrentUser().role()
        );

        return role == RoleName.ADMIN
                || role == RoleName.MANAGER;
    }

    @Override
    public boolean canAddManualField() {

        RoleName role = RoleName.valueOf(
                currentUserService.getCurrentUser().role()
        );

        return role != RoleName.APPROVER;
    }

    private void throwForbidden(String message) {

        ValidationErrors errors = new ValidationErrors();

        errors.add(
                "FORBIDDEN",
                message
        );

        throw new ApiValidationException(errors);
    }
}
