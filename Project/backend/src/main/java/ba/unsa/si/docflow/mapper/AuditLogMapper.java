package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.audit.AuditLogResponse;
import ba.unsa.si.docflow.entity.AuditLogEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuditLogMapper {

    private final UserDAO userDAO;

    public AuditLogResponse toResponse(AuditLogEntity entity) {
        UserEntity user = entity.getUserId() == null ? null : userDAO.findByPK(entity.getUserId());

        String fullName = user == null
                ? null
                : user.getFirstName() + " " + user.getLastName();

        return AuditLogResponse.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .details(entity.getDetails())
                .timestamp(entity.getTimestamp())
                .userId(entity.getUserId())
                .userFullName(fullName)
                .build();
    }
}
