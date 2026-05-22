package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.NotificationEntity;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationDAO extends AbstractDAO<NotificationEntity, Long> {

    public List<NotificationEntity> findByUserIdOrderByCreatedAt(Long userId) {
        String jpql =
                """
                SELECT n
                FROM NotificationEntity n
                LEFT JOIN FETCH n.document
                LEFT JOIN FETCH n.comment
                WHERE n.userId = :userId
                ORDER BY n.createdAt DESC, n.id DESC
                """;

        return entityManager
                .createQuery(jpql, NotificationEntity.class)
                .setParameter("userId", userId)
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

    public List<NotificationEntity> findUnreadByUserId(Long userId) {
        String jpql =
                """
                SELECT n
                FROM NotificationEntity n
                LEFT JOIN FETCH n.document
                LEFT JOIN FETCH n.comment
                WHERE n.userId = :userId
                AND n.read = false
                ORDER BY n.createdAt DESC, n.id DESC
                """;

        return entityManager
                .createQuery(jpql, NotificationEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void deleteByDocumentId(Long documentId) {
        entityManager
                .createQuery(
                        "DELETE FROM NotificationEntity n WHERE n.document.id = :documentId")
                .setParameter("documentId", documentId)
                .executeUpdate();
    }
}
