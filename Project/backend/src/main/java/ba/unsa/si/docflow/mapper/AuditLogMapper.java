package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.audit.AuditLogResponse;
import ba.unsa.si.docflow.entity.AuditLogEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLogEntity entity) {

        return AuditLogResponse.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .details(entity.getDetails())
                .timestamp(entity.getTimestamp())
                .userId(entity.getUserId())
                .build();
    }
}
