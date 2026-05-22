package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.CommentEntity;
import ba.unsa.si.docflow.entity.enums.CommentType;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDAO extends AbstractDAO<CommentEntity, Long> {

    public List<CommentEntity> findByDocumentIdOrderByCreatedAt(Long documentId) {
        String jpql =
                """
                SELECT c
                FROM CommentEntity c
                WHERE c.document.id = :documentId
                ORDER BY c.createdAt ASC, c.id ASC
                """;

        return entityManager
                .createQuery(jpql, CommentEntity.class)
                .setParameter("documentId", documentId)
                .getResultList();
    }

    public CommentEntity findLatestByDocumentIdAndType(Long documentId, CommentType type) {
        String jpql =
                """
                SELECT c
                FROM CommentEntity c
                WHERE c.document.id = :documentId
                AND c.type = :type
                ORDER BY c.createdAt DESC, c.id DESC
                """;

        return entityManager
                .createQuery(jpql, CommentEntity.class)
                .setParameter("documentId", documentId)
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
