package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.StatusHistoryEntity;
import ba.unsa.si.docflow.entity.enums.StatusHistoryAction;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatusHistoryDAO extends AbstractDAO<StatusHistoryEntity, Long> {

    public List<StatusHistoryEntity> findByDocumentIdOrderByChangedAt(Long documentId) {
        String jpql =
                """
                SELECT sh
                FROM StatusHistoryEntity sh
                LEFT JOIN FETCH sh.comment
                WHERE sh.document.id = :documentId
                ORDER BY sh.changedAt ASC, sh.id ASC
                """;

        return entityManager
                .createQuery(jpql, StatusHistoryEntity.class)
                .setParameter("documentId", documentId)
                .getResultList();
    }

    public StatusHistoryEntity findLatestByDocumentIdAndAction(
            Long documentId, StatusHistoryAction action) {
        String jpql =
                """
                SELECT sh
                FROM StatusHistoryEntity sh
                WHERE sh.document.id = :documentId
                AND sh.action = :action
                ORDER BY sh.changedAt DESC, sh.id DESC
                """;

        TypedQuery<StatusHistoryEntity> query =
                entityManager.createQuery(jpql, StatusHistoryEntity.class);
        query.setParameter("documentId", documentId);
        query.setParameter("action", action);
        query.setMaxResults(1);

        return query.getResultStream().findFirst().orElse(null);
    }

    public void deleteByDocumentId(Long documentId) {
        entityManager
                .createQuery(
                        "DELETE FROM StatusHistoryEntity sh WHERE sh.document.id = :documentId")
                .setParameter("documentId", documentId)
                .executeUpdate();
    }
}
