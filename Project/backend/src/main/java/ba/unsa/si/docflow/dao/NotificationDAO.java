package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.NotificationEntity;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class NotificationDAO extends AbstractDAO<NotificationEntity, Long> {

    public List<NotificationEntity> findByUserId(Long userId) {
        String jpql =
                """
                SELECT n
                FROM NotificationEntity n
                WHERE n.userId = :userId
                ORDER BY n.createdAt DESC, n.id DESC
                """;

        return entityManager
                .createQuery(jpql, NotificationEntity.class)
                .setParameter("userId", userId)
                .setMaxResults(100)
                .getResultList();
    }

    public long countUnreadByUserId(Long userId) {
        String jpql =
                """
                SELECT COUNT(n.id)
                FROM NotificationEntity n
                WHERE n.userId = :userId
                AND n.read = false
                """;

        return entityManager
                .createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    public NotificationEntity findByIdAndUserId(Long id, Long userId) {
        String jpql =
                """
                SELECT n
                FROM NotificationEntity n
                WHERE n.id = :id
                AND n.userId = :userId
                """;

        return entityManager
                .createQuery(jpql, NotificationEntity.class)
                .setParameter("id", id)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public int markAllReadForUser(Long userId, Instant now) {
        String jpql =
                """
                UPDATE NotificationEntity n
                SET n.read = true, n.readAt = :now
                WHERE n.userId = :userId
                AND n.read = false
                """;

        return entityManager
                .createQuery(jpql)
                .setParameter("now", now)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    public Map<Long, List<NotificationEntity>> findUnreadOlderThanWithNoEmail(Instant threshold) {
        String jpql =
                """
                SELECT n
                FROM NotificationEntity n
                WHERE n.read = false
                AND n.emailSentAt IS NULL
                AND n.createdAt < :threshold
                """;

        List<NotificationEntity> list = entityManager
                .createQuery(jpql, NotificationEntity.class)
                .setParameter("threshold", threshold)
                .getResultList();

        return list.stream().collect(Collectors.groupingBy(NotificationEntity::getUserId));
    }

    public void deleteByDocumentId(Long documentId) {
        entityManager
                .createQuery("DELETE FROM NotificationEntity n WHERE n.documentId = :documentId")
                .setParameter("documentId", documentId)
                .executeUpdate();
    }
}
