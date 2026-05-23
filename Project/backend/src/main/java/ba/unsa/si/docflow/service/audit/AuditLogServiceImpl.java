package ba.unsa.si.docflow.service.audit;

import ba.unsa.si.docflow.dao.AuditLogDAO;
import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dto.audit.AuditLogResponse;
import ba.unsa.si.docflow.entity.AuditLogEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.AuditAction;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.mapper.AuditLogMapper;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.service.security.WorkflowPermissionService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogDAO auditLogDAO;
    private final DocumentDAO documentDAO;
    private final AuditLogMapper auditLogMapper;
    private final WorkflowPermissionService permissionService;

    @Override
    public void log(
            DocumentEntity document,
            Long userId,
            AuditAction action,
            String details
    ) {

        AuditLogEntity entity = new AuditLogEntity();

        entity.setDocument(document);
        entity.setUserId(userId);
        entity.setAction(action);
        entity.setDetails(details);

        auditLogDAO.persist(entity);
    }

    @Override
    public List<AuditLogResponse> getDocumentAuditLog(
            Long documentId
    ) {

        DocumentEntity document =
                documentDAO.findByPK(documentId);

        if (document == null) {
            ValidationErrors errors = new ValidationErrors();

            errors.add(
                    "DOCUMENT_NOT_FOUND",
                    "Document not found with id: " + documentId
            );

            throw new ApiValidationException(errors);
        }

        permissionService.requireCanViewAudit(document);

        return auditLogDAO
                .findByDocumentIdOrderByTimestamp(documentId)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }
}
