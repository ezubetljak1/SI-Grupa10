package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.TaskEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.entity.enums.TaskStatus;
import ba.unsa.si.docflow.dto.dashboard.DocumentsByResponsibleUserDto;

import jakarta.persistence.Tuple;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

        List<Predicate> predicates = buildPredicates(request, cb, cq, root);
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

        List<Predicate> predicates = buildPredicates(request, cb, cq, root);
        cq.select(cb.count(root));
        cq.where(predicates.toArray(new Predicate[0]));

        return executeCountQuery(cq);
    }

    public List<DocumentEntity> findByStatusAndCompanyId(DocumentStatus status, Long companyId) {
        String jpql = """
                SELECT d
                FROM DocumentEntity d
                WHERE d.documentStatus = :status
                AND d.companyId = :companyId
                ORDER BY d.uploadDate ASC, d.id ASC
                """;

        return entityManager
                .createQuery(jpql, DocumentEntity.class)
                .setParameter("status", status)
                .setParameter("companyId", companyId)
                .getResultList();
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
            DocumentFilterRequest request,
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<DocumentEntity> root) {
        List<Predicate> predicates = new ArrayList<>();

        String searchTerm = resolveSearchTerm(request);
        addSearchPredicate(predicates, cb, root, searchTerm);

        addDateRangePredicates(predicates, cb, root, request);

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

        addAssignedUserPredicate(predicates, cb, cq, root, request.getAssignedUserId());
        addEqualIfNotNull(predicates, cb, root.get("companyId"), request.getCompanyId());

        return predicates;
    }

    private String resolveSearchTerm(DocumentFilterRequest request) {
        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            return request.getSearch();
        }

        return request.getName();
    }

    private void addSearchPredicate(
            List<Predicate> predicates,
            CriteriaBuilder cb,
            Root<DocumentEntity> root,
            String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return;
        }

        String normalized = searchTerm.trim();
        List<Predicate> searchPredicates = new ArrayList<>();

        searchPredicates.add(likeIgnoreCase(cb, root.get("name"), normalized));

        if (isNumeric(normalized)) {
            searchPredicates.add(cb.equal(root.get("id"), Long.valueOf(normalized)));
        }

        if (!searchPredicates.isEmpty()) {
            predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
        }
    }

    private void addDateRangePredicates(
            List<Predicate> predicates,
            CriteriaBuilder cb,
            Root<DocumentEntity> root,
            DocumentFilterRequest request) {
        if (request.getCreatedFrom() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(
                            root.get("uploadDate"),
                            request.getCreatedFrom().atStartOfDay()));
        }

        if (request.getCreatedTo() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(
                            root.get("uploadDate"),
                            request.getCreatedTo().atTime(LocalTime.MAX)));
        }
    }

    private void addAssignedUserPredicate(
            List<Predicate> predicates,
            CriteriaBuilder cb,
            CriteriaQuery<?> cq,
            Root<DocumentEntity> root,
            Long assignedUserId) {
        if (assignedUserId == null) {
            return;
        }

        Subquery<Long> taskExistsQuery = cq.subquery(Long.class);
        Root<TaskEntity> taskRoot = taskExistsQuery.from(TaskEntity.class);

        taskExistsQuery.select(cb.literal(1L));
        taskExistsQuery.where(
                cb.equal(taskRoot.get("document").get("id"), root.get("id")),
                cb.equal(taskRoot.get("assignedUserId"), assignedUserId),
                taskRoot.get("status").in(TaskStatus.OPEN, TaskStatus.IN_PROGRESS));

        predicates.add(cb.exists(taskExistsQuery));
    }

    private boolean isNumeric(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
