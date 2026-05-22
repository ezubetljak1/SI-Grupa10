package ba.unsa.si.docflow.entity;

import ba.unsa.si.docflow.entity.enums.NotificationType;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "notification",
        indexes = {
            @Index(name = "idx_notification_user_id", columnList = "user_id"),
            @Index(name = "idx_notification_document_id", columnList = "document_id"),
            @Index(name = "idx_notification_is_read", columnList = "is_read"),
            @Index(name = "idx_notification_created_at", columnList = "created_at"),
            @Index(name = "idx_notification_email_sent_at", columnList = "email_sent_at")
        })
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Recipient user id. */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 80)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    /** Frontend route, for example: /documents/12 /review /tasks/my */
    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean default false")
    private Boolean read = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (read == null) {
            read = false;
        }
    }
}
