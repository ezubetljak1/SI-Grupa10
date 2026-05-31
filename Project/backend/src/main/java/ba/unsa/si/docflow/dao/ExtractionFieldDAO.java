package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.ExtractionFieldEntity;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExtractionFieldDAO extends AbstractDAO<ExtractionFieldEntity, Long> {

    public List<ExtractionFieldEntity> findByExtractionId(Long extractionId) {
        String jpql =
                """
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
        String jpql =
                """
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

    public Optional<ExtractionFieldEntity> findByIdAndExtractionId(
            Long fieldId, Long extractionId) {
        String jpql =
                """
        SELECT ef
        FROM ExtractionFieldEntity ef
        WHERE ef.id = :fieldId
        AND ef.extraction.id = :extractionId
        """;

        TypedQuery<ExtractionFieldEntity> query =
                entityManager.createQuery(jpql, ExtractionFieldEntity.class);

        query.setParameter("fieldId", fieldId);
        query.setParameter("extractionId", extractionId);

        return query.getResultStream().findFirst();
    }

    public boolean existsByExtractionIdAndFieldName(Long extractionId, String fieldName) {
        String jpql =
                """
                SELECT COUNT(ef)
                FROM ExtractionFieldEntity ef
                WHERE ef.extraction.id = :extractionId
                  AND LOWER(ef.fieldName) = LOWER(:fieldName)
                """;

        Long count =
                entityManager
                        .createQuery(jpql, Long.class)
                        .setParameter("extractionId", extractionId)
                        .setParameter("fieldName", fieldName)
                        .getSingleResult();

        return count != null && count > 0;
    }
}
