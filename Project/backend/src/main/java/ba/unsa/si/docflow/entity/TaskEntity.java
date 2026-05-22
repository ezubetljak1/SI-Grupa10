package ba.unsa.si.docflow.entity;

import ba.unsa.si.docflow.entity.enums.TaskStatus;
import ba.unsa.si.docflow.entity.enums.TaskType;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "workflow_task",
        indexes = {
            @Index(name = "idx_workflow_task_document_id", columnList = "document_id"),
            @Index(name = "idx_workflow_task_assigned_user_id", columnList = "assigned_user_id"),
            @Index(
                    name = "idx_workflow_task_assigned_by_user_id",
                    columnList = "assigned_by_user_id"),
            @Index(name = "idx_workflow_task_status", columnList = "status"),
            @Index(name = "idx_workflow_task_task_type", columnList = "task_type")
        })
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Task is tied to a document, not stored directly on DocumentEntity. This allows multiple
     * lifecycle responsibilities over time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    @Column(name = "assigned_user_id", nullable = false)
    private Long assignedUserId;

    @Column(name = "assigned_by_user_id", nullable = false)
    private Long assignedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 50)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TaskStatus status = TaskStatus.OPEN;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by_user_id")
    private Long completedByUserId;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = TaskStatus.OPEN;
        }
    }
}
