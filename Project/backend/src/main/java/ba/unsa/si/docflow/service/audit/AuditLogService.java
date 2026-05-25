package ba.unsa.si.docflow.service.audit;

import ba.unsa.si.docflow.dto.audit.AuditLogResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.AuditAction;

import java.util.List;

public interface AuditLogService {

    void log(
            DocumentEntity document,
            Long userId,
            AuditAction action,
            String details
    );

    List<AuditLogResponse> getDocumentAuditLog(Long documentId);
}
