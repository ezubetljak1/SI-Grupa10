package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.enums.RoleName;

import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDAO extends AbstractDAO<RoleEntity, Long> {

    public RoleEntity findByName(RoleName name) {
        String jpql = "SELECT r FROM RoleEntity r WHERE r.name = :name";

        TypedQuery<RoleEntity> query = entityManager.createQuery(jpql, RoleEntity.class);
        query.setParameter("name", name);
        query.setMaxResults(1);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public List<RoleEntity> findAllOrderedByName() {
        String jpql = "SELECT r FROM RoleEntity r ORDER BY r.name ASC";

        return entityManager.createQuery(jpql, RoleEntity.class).getResultList();
    }
}
