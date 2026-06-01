package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.XmlOutputEntity;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

@Repository
public class XmlOutputDAO extends AbstractDAO<XmlOutputEntity, Long> {

    public XmlOutputEntity findByDocumentId(Long documentId) {
        String jpql =
                """
                SELECT xo
                FROM XmlOutputEntity xo
                WHERE xo.document.id = :documentId
                """;

        TypedQuery<XmlOutputEntity> query = entityManager.createQuery(jpql, XmlOutputEntity.class);

        query.setParameter("documentId", documentId);

        return query.getResultStream().findFirst().orElse(null);
    }

    public void deleteByDocumentId(Long documentId) {
        entityManager
                .createQuery(
                        """
                        DELETE FROM XmlOutputEntity xo
                        WHERE xo.document.id = :documentId
                        """)
                .setParameter("documentId", documentId)
                .executeUpdate();
    }
}
