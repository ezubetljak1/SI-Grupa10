package ba.unsa.si.docflow.entity;

import ba.unsa.si.docflow.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "notification",
        indexes = {
            @Index(name = "idx_notification_user_id", columnList = "user_id"),
            @Index(name = "idx_notification_document_id", columnList = "document_id"),
            @Index(name = "idx_notification_is_read", columnList = "is_read"),
            @Index(name = "idx_notification_created_at", columnList = "created_at")
        })
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "comment_id")
    private Long commentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 80)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "text", nullable = false, length = 500)
    private String text;

    @Column(name = "action_url", length = 300)
    private String actionUrl;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "email_sent_at")
    private Instant emailSentAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
