package com.rosgard.repository;

import com.rosgard.entity.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CoordinatesRepository {

    @PersistenceContext(unitName = "IS1")
    private EntityManager em;

    public Optional<Coordinates> findById(int id) {
        return Optional.ofNullable(em.find(Coordinates.class, id));
    }

    public List<Coordinates> findAll() {
        return em.createQuery("SELECT c FROM Coordinates c ORDER BY c.id", Coordinates.class)
                .getResultList();
    }

    public List< Object[]> findAllWithUsage() {
        return em.createQuery(
                        "SELECT c.id, c.x, c.y, COUNT(v) " +
                                "FROM Coordinates c LEFT JOIN Vehicle v ON v.coordinates = c " +
                                "GROUP BY c.id, c.x, c.y ORDER BY c.id", Object[].class)
                .getResultList();
    }

    public void delete(Coordinates c) {
        em.remove(c);
    }
}