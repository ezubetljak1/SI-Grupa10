package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.AuditLogEntity;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogDAO extends AbstractDAO<AuditLogEntity, Long> {

    public List<AuditLogEntity> findByDocumentIdOrderByTimestamp(Long documentId) {
        String jpql =
                """
                SELECT a
                FROM AuditLogEntity a
                WHERE a.document.id = :documentId
                ORDER BY a.timestamp DESC, a.id DESC
                """;

        return entityManager
                .createQuery(jpql, AuditLogEntity.class)
                .setParameter("documentId", documentId)
                .getResultList();
    }

    public void deleteByDocumentId(Long documentId) {
        entityManager
                .createQuery("DELETE FROM AuditLogEntity a WHERE a.document.id = :documentId")
                .setParameter("documentId", documentId)
                .executeUpdate();
    }
}
