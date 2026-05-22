package ba.unsa.si.docflow.entity;

import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.StatusHistoryAction;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "status_history",
        indexes = {
            @Index(name = "idx_status_history_document_id", columnList = "document_id"),
            @Index(
                    name = "idx_status_history_changed_by_user_id",
                    columnList = "changed_by_user_id"),
            @Index(name = "idx_status_history_changed_at", columnList = "changed_at")
        })
public class StatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Status history belongs to one document. It is append-only from the application perspective.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    /** Nullable for the initial history row, for example DOCUMENT_UPLOADED. */
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 50)
    private DocumentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 50)
    private DocumentStatus newStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 100)
    private StatusHistoryAction action;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by_user_id", nullable = false)
    private Long changedByUserId;

    /**
     * Optional comment connected with a business decision. Example: rejection reason or correction
     * request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @PrePersist
    void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}
