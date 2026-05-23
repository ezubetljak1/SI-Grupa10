package ba.unsa.si.docflow.dto.audit;

import ba.unsa.si.docflow.entity.enums.AuditAction;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuditLogResponse(
        Long id,
        AuditAction action,
        String details,
        LocalDateTime timestamp,
        Long userId,
        String userFullName
) {
}
