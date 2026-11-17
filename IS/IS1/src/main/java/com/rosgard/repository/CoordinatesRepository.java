package com.rosgard.repository;

import com.rosgard.entity.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CoordinatesRepository {

    @PersistenceContext(unitName = "IS1")
    private EntityManager em;

    public Optional<Coordinates> findById(int id) {
        return Optional.ofNullable(em.find(Coordinates.class, id));
    }

    public List<Coordinates> findPage(int offset, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Coordinates> q = cb.createQuery(Coordinates.class);
        Root<Coordinates> c = q.from(Coordinates.class);
        q.orderBy(cb.asc(c.get("id")));
        return em.createQuery(q)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }

    public long count() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        q.select(cb.count(q.from(Coordinates.class)));
        return em.createQuery(q).getSingleResult();
    }

    public void delete(Coordinates c) {
        em.remove(c);
    }
}
