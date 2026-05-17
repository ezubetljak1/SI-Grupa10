package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.UserEntity;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

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
}
