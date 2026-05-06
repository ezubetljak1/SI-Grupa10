package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.ExtractionEntity;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

@Repository
public class ExtractionDAO extends AbstractDAO<ExtractionEntity, Long> {

    public ExtractionEntity findByDocumentId(Long documentId) {
        String jpql = """
                SELECT e
                FROM ExtractionEntity e
                LEFT JOIN FETCH e.fields
                WHERE e.document.id = :documentId
                """;

        TypedQuery<ExtractionEntity> query =
                entityManager.createQuery(jpql, ExtractionEntity.class);

        query.setParameter("documentId", documentId);

        return query.getResultStream().findFirst().orElse(null);
    }
}