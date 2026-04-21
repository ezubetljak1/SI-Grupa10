package ba.unsa.si.docflow.dao;

import ba.unsa.si.docflow.dto.BaseFilterRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDAO<T, K extends Serializable> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    protected AbstractDAO() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public T persist(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T merge(T entity) {
        return entityManager.merge(entity);
    }

    public void remove(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    public T findByPK(K id) {
        return entityManager.find(entityClass, id);
    }

    public boolean existsByPK(K id) {
        return findByPK(id) != null;
    }

    protected List<T> executePagedQuery(
            CriteriaQuery<T> cq,
            BaseFilterRequest request
    ) {
        TypedQuery<T> query = entityManager.createQuery(cq);
        query.setFirstResult(request.getPage() * request.getSize());
        query.setMaxResults(request.getSize());
        return query.getResultList();
    }

    protected long executeCountQuery(
            CriteriaQuery<Long> countQuery
    ) {
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    protected void applySorting(
            CriteriaBuilder cb,
            CriteriaQuery<T> cq,
            Root<T> root,
            String sortBy,
            String sortDirection,
            String defaultSortField,
            List<String> allowedSortFields
    ) {
        String resolvedSortBy = defaultSortField;

        if (sortBy != null && allowedSortFields.contains(sortBy)) {
            resolvedSortBy = sortBy;
        }

        boolean asc = !"desc".equalsIgnoreCase(sortDirection);

        if (asc) {
            cq.orderBy(cb.asc(root.get(resolvedSortBy)));
        } else {
            cq.orderBy(cb.desc(root.get(resolvedSortBy)));
        }
    }

    protected Predicate likeIgnoreCase(
            CriteriaBuilder cb,
            Path<String> path,
            String value
    ) {
        return cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
    }

    protected <V> void addEqualIfNotNull(
            List<Predicate> predicates,
            CriteriaBuilder cb,
            Path<V> path,
            V value
    ) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    protected void addLikeIfNotBlank(
            List<Predicate> predicates,
            CriteriaBuilder cb,
            Path<String> path,
            String value
    ) {
        if (value != null && !value.isBlank()) {
            predicates.add(likeIgnoreCase(cb, path, value));
        }
    }
}