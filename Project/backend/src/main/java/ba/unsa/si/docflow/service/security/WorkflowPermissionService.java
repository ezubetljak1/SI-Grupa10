package ba.unsa.si.docflow.service.security;

import ba.unsa.si.docflow.entity.DocumentEntity;

public interface WorkflowPermissionService {

    void requireCanViewAudit(DocumentEntity document);

    void requireCanAssignTask(DocumentEntity document);

    void requireCanViewNotifications();

    void requireCanApprove(DocumentEntity document);

    void requireCanEditExtraction(DocumentEntity document);

    void requireCanRunExtraction(DocumentEntity document);

    void requireCanConfirmExtraction(DocumentEntity document);

    void requireSameCompany(Long companyId);

    boolean canViewAudit();

    boolean canApprove();

    boolean canManageExtraction();
    boolean canViewAllTasks();

    boolean canAddManualField();
}