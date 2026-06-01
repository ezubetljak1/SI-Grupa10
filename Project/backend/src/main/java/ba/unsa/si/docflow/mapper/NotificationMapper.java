package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.notification.NotificationResponse;
import ba.unsa.si.docflow.entity.NotificationEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .documentId(entity.getDocumentId())
                .commentId(entity.getCommentId())
                .type(entity.getType())
                .title(entity.getTitle())
                .text(entity.getText())
                .actionUrl(entity.getActionUrl())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .emailSentAt(entity.getEmailSentAt())
                .build();
    }

    public List<NotificationResponse> toResponseList(List<NotificationEntity> list) {
        if (list == null) {
            return List.of();
        }

        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
