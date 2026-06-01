package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.dto.user.UserFilterRequest;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAO extends AbstractDAO<UserEntity, Long> {

    public UserEntity findByKeycloakUserId(String keycloakUserId) {
        String jpql = "SELECT u FROM UserEntity u WHERE u.keycloakUserId = :keycloakUserId";

        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        query.setParameter("keycloakUserId", keycloakUserId);
        query.setMaxResults(1);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public boolean existsByEmailAndCompanyId(String email, Long companyId, Long excludeId) {
        String jpql =
                """
                SELECT COUNT(u)
                FROM UserEntity u
                WHERE LOWER(u.email) = LOWER(:email)
                AND u.companyId = :companyId
                AND (:excludeId IS NULL OR u.id <> :excludeId)
                """;

        Long count =
                entityManager
                        .createQuery(jpql, Long.class)
                        .setParameter("email", email)
                        .setParameter("companyId", companyId)
                        .setParameter("excludeId", excludeId)
                        .getSingleResult();

        return count > 0;
    }

    public UserEntity findByIdAndCompanyId(Long id, Long companyId) {
        String jpql = "SELECT u FROM UserEntity u WHERE u.id = :id AND u.companyId = :companyId";

        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        query.setParameter("id", id);
        query.setParameter("companyId", companyId);
        query.setMaxResults(1);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public List<UserEntity> findByFilter(UserFilterRequest request, Long companyId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        Root<UserEntity> root = cq.from(UserEntity.class);

        List<Predicate> predicates = buildPredicates(request, companyId, cb, root);
        cq.where(predicates.toArray(new Predicate[0]));

        applySorting(
                cb,
                cq,
                root,
                request.getSortBy(),
                request.getSortDirection(),
                "id",
                List.of("id", "firstName", "lastName", "email", "accountStatus", "createdAt"));

        return executePagedQuery(cq, request);
    }

    public long countByFilter(UserFilterRequest request, Long companyId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<UserEntity> root = cq.from(UserEntity.class);

        List<Predicate> predicates = buildPredicates(request, companyId, cb, root);
        cq.select(cb.count(root));
        cq.where(predicates.toArray(new Predicate[0]));

        return executeCountQuery(cq);
    }

    private List<Predicate> buildPredicates(
            UserFilterRequest request, Long companyId, CriteriaBuilder cb, Root<UserEntity> root) {

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("companyId"), companyId));

        if (request.getSearch() != null && !request.getSearch().isBlank()) {
            String pattern = "%" + request.getSearch().toLowerCase() + "%";
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("firstName")), pattern),
                            cb.like(cb.lower(root.get("lastName")), pattern),
                            cb.like(cb.lower(root.get("email")), pattern)));
        }

        addEqualIfNotNull(predicates, cb, root.get("accountStatus"), request.getAccountStatus());

        return predicates;
    }

    public long countActiveAdminsByCompanyId(Long companyId) {
        String jpql =
                """
        SELECT COUNT(u)
        FROM UserEntity u, RoleEntity r
        WHERE u.roleId = r.id
          AND u.companyId = :companyId
          AND r.name = :adminRole
          AND u.accountStatus <> :inactiveStatus
    """;

        return entityManager
                .createQuery(jpql, Long.class)
                .setParameter("companyId", companyId)
                .setParameter("adminRole", RoleName.ADMIN)
                .setParameter("inactiveStatus", AccountStatus.INACTIVE)
                .getSingleResult();
    }

    public List<UserEntity> findActiveByCompanyIdAndRoleName(Long companyId, RoleName roleName) {

        String jpql =
                """
                SELECT u
                FROM UserEntity u, RoleEntity r
                WHERE u.roleId = r.id
                  AND u.companyId = :companyId
                  AND r.name = :roleName
                  AND u.accountStatus <> :inactiveStatus
                ORDER BY u.id
                """;

        return entityManager
                .createQuery(jpql, UserEntity.class)
                .setParameter("companyId", companyId)
                .setParameter("roleName", roleName)
                .setParameter("inactiveStatus", AccountStatus.INACTIVE)
                .getResultList();
    }
}
