package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.dto.dashboard.DocumentsByResponsibleUserDto;

import jakarta.persistence.Tuple;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DocumentDAO extends AbstractDAO<DocumentEntity, Long> {
    public Boolean existsByNameInCompany(String name, Long companyId, Long id) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT CASE WHEN COUNT(d.id) > 0 THEN TRUE ELSE FALSE END ");
        jpql.append("FROM DocumentEntity d ");
        jpql.append("WHERE UPPER(d.name) = UPPER(:name) ");
        jpql.append("AND d.companyId = :companyId ");
        jpql.append("AND (:id IS NULL OR d.id <> :id)");

        TypedQuery<Boolean> query = entityManager.createQuery(jpql.toString(), Boolean.class);
        query.setParameter("name", name);
        query.setParameter("companyId", companyId);
        query.setParameter("id", id);

        return query.getSingleResult();
    }

    public List<DocumentEntity> findByFilter(DocumentFilterRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DocumentEntity> cq = cb.createQuery(DocumentEntity.class);
        Root<DocumentEntity> root = cq.from(DocumentEntity.class);

        List<Predicate> predicates = buildPredicates(request, cb, root);
        cq.where(predicates.toArray(new Predicate[0]));

        applySorting(
                cb,
                cq,
                root,
                request.getSortBy(),
                request.getSortDirection(),
                "id",
                List.of(
                        "id",
                        "name",
                        "uploadDate",
                        "fileSize",
                        "documentStatus",
                        "documentType",
                        "companyId"));

        return executePagedQuery(cq, request);
    }

    public long countByFilter(DocumentFilterRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DocumentEntity> root = cq.from(DocumentEntity.class);

        List<Predicate> predicates = buildPredicates(request, cb, root);
        cq.select(cb.count(root));
        cq.where(predicates.toArray(new Predicate[0]));

        return executeCountQuery(cq);
    }

    public Long countByCompanyId(Long companyId) {

        String jpql = """
            SELECT COUNT(d.id)
            FROM DocumentEntity d
            WHERE d.companyId = :companyId
            """;

        return entityManager
                .createQuery(jpql, Long.class)
                .setParameter("companyId", companyId)
                .getSingleResult();
    }

    public Map<String, Long> countDocumentsByStatus(Long companyId) {

        String jpql = """
            SELECT d.documentStatus, COUNT(d.id)
            FROM DocumentEntity d
            WHERE d.companyId = :companyId
            GROUP BY d.documentStatus
            """;

        List<Object[]> results = entityManager
                .createQuery(jpql, Object[].class)
                .setParameter("companyId", companyId)
                .getResultList();

        Map<String, Long> map = new HashMap<>();

        for (Object[] row : results) {

            map.put(
                    row[0].toString(),
                    (Long) row[1]
            );
        }

        return map;
    }

    public List<DocumentsByResponsibleUserDto> countDocumentsByResponsibleUser(
            Long companyId
    ) {

        String jpql = """
            SELECT u.id,
                   CONCAT(u.firstName, ' ', u.lastName),
                   COUNT(d.id)
            FROM DocumentEntity d
            JOIN UserEntity u ON u.id = d.createdBy
            WHERE d.companyId = :companyId
            GROUP BY u.id, u.firstName, u.lastName
            ORDER BY COUNT(d.id) DESC
            """;

        List<Object[]> results = entityManager
                .createQuery(jpql, Object[].class)
                .setParameter("companyId", companyId)
                .getResultList();

        return results.stream()
                .map(row -> new DocumentsByResponsibleUserDto(
                        (Long) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .toList();
    }

    private List<Predicate> buildPredicates(
            DocumentFilterRequest request, CriteriaBuilder cb, Root<DocumentEntity> root) {
        List<Predicate> predicates = new ArrayList<>();

        addLikeIfNotBlank(predicates, cb, root.get("name"), request.getName());

        if (request.getDocumentType() != null && !request.getDocumentType().isBlank()) {
            predicates.add(
                    cb.equal(
                            root.get("documentType"),
                            DocumentType.valueOf(request.getDocumentType().toUpperCase())));
        }

        if (request.getDocumentStatus() != null && !request.getDocumentStatus().isBlank()) {
            predicates.add(
                    cb.equal(
                            root.get("documentStatus"),
                            DocumentStatus.valueOf(request.getDocumentStatus().toUpperCase())));
        }

        addEqualIfNotNull(predicates, cb, root.get("companyId"), request.getCompanyId());

        return predicates;
    }
}
