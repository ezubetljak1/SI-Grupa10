package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.ExtractionFieldEntity;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExtractionFieldDAO extends AbstractDAO<ExtractionFieldEntity, Long> {

    public List<ExtractionFieldEntity> findByExtractionId(Long extractionId) {
        String jpql = """
                SELECT ef
                FROM ExtractionFieldEntity ef
                WHERE ef.extraction.id = :extractionId
                ORDER BY ef.fieldName ASC
                """;

        TypedQuery<ExtractionFieldEntity> query =
                entityManager.createQuery(jpql, ExtractionFieldEntity.class);

        query.setParameter("extractionId", extractionId);

        return query.getResultList();
    }

    public List<ExtractionFieldEntity> findByDocumentId(Long documentId) {
        String jpql = """
                SELECT ef
                FROM ExtractionFieldEntity ef
                WHERE ef.extraction.document.id = :documentId
                ORDER BY ef.fieldName ASC
                """;

        TypedQuery<ExtractionFieldEntity> query =
                entityManager.createQuery(jpql, ExtractionFieldEntity.class);

        query.setParameter("documentId", documentId);

        return query.getResultList();
    }
}