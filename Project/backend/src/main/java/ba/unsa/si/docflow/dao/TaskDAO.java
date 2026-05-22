package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.TaskEntity;
import ba.unsa.si.docflow.entity.enums.TaskStatus;
import ba.unsa.si.docflow.entity.enums.TaskType;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskDAO extends AbstractDAO<TaskEntity, Long> {

    private static final List<TaskStatus> ACTIVE_STATUSES =
            List.of(TaskStatus.OPEN, TaskStatus.IN_PROGRESS);

    public List<TaskEntity> findByAssignedUserId(Long assignedUserId) {
        String jpql =
                """
                SELECT t
                FROM TaskEntity t
                JOIN FETCH t.document
                WHERE t.assignedUserId = :assignedUserId
                ORDER BY t.createdAt DESC, t.id DESC
                """;

        return entityManager
                .createQuery(jpql, TaskEntity.class)
                .setParameter("assignedUserId", assignedUserId)
                .getResultList();
    }

    public List<TaskEntity> findActiveByAssignedUserId(Long assignedUserId) {
        String jpql =
                """
                SELECT t
                FROM TaskEntity t
                JOIN FETCH t.document
                WHERE t.assignedUserId = :assignedUserId
                AND t.status IN :statuses
                ORDER BY t.createdAt DESC, t.id DESC
                """;

        return entityManager
                .createQuery(jpql, TaskEntity.class)
                .setParameter("assignedUserId", assignedUserId)
                .setParameter("statuses", ACTIVE_STATUSES)
                .getResultList();
    }

    public TaskEntity findActiveByDocumentIdAndTaskType(Long documentId, TaskType taskType) {
        String jpql =
                """
                SELECT t
                FROM TaskEntity t
                WHERE t.document.id = :documentId
                AND t.taskType = :taskType
                AND t.status IN :statuses
                ORDER BY t.createdAt DESC, t.id DESC
                """;

        return entityManager
                .createQuery(jpql, TaskEntity.class)
                .setParameter("documentId", documentId)
                .setParameter("taskType", taskType)
                .setParameter("statuses", ACTIVE_STATUSES)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
