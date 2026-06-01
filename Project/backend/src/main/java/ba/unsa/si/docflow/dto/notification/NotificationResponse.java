package ba.unsa.si.docflow.dto.notification;

import ba.unsa.si.docflow.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private Long documentId;
    private Long commentId;
    private NotificationType type;
    private String title;
    private String text;
    private String actionUrl;
    private boolean read;
    private Instant createdAt;
    private Instant readAt;
    private Instant emailSentAt;
}
