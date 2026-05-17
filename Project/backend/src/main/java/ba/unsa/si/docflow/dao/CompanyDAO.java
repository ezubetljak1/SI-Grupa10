package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.CompanyEntity;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

@Repository
public class CompanyDAO extends AbstractDAO<CompanyEntity, Long> {

    public boolean existsByEmail(String email, Long excludeId) {
        String jpql =
                """
                SELECT COUNT(c)
                FROM CompanyEntity c
                WHERE LOWER(c.email) = LOWER(:email)
                AND (:excludeId IS NULL OR c.id <> :excludeId)
                """;

        Long count =
                entityManager
                        .createQuery(jpql, Long.class)
                        .setParameter("email", email)
                        .setParameter("excludeId", excludeId)
                        .getSingleResult();

        return count > 0;
    }

    public CompanyEntity findByEmail(String email) {
        String jpql = "SELECT c FROM CompanyEntity c WHERE LOWER(c.email) = LOWER(:email)";

        TypedQuery<CompanyEntity> query =
                entityManager.createQuery(jpql, CompanyEntity.class);
        query.setParameter("email", email);
        query.setMaxResults(1);

        return query.getResultList().stream().findFirst().orElse(null);
    }
}
