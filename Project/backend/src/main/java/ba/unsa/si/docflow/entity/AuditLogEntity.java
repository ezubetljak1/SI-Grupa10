package ba.unsa.si.docflow.entity;

import ba.unsa.si.docflow.entity.enums.AuditAction;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "audit_log",
        indexes = {
            @Index(name = "idx_audit_log_document_id", columnList = "document_id"),
            @Index(name = "idx_audit_log_user_id", columnList = "user_id"),
            @Index(name = "idx_audit_log_action", columnList = "action"),
            @Index(name = "idx_audit_log_timestamp", columnList = "timestamp")
        })
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nullable because some audit actions can be user/system level, not necessarily tied to a
     * document.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 100)
    private AuditAction action;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Store safe details only. Never store passwords, tokens, SMTP secrets or full OAuth claims
     * here.
     */
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @PrePersist
    void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
