package com.rosgard.repository;

import com.rosgard.entity.FuelType;
import com.rosgard.entity.Vehicle;
import com.rosgard.entity.VehicleType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class VehicleRepository {

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "creationDate", "type", "enginePower", "numberOfWheels",
            "capacity", "distanceTravelled", "fuelConsumption", "fuelType", "coordinates.x", "coordinates.y");

    @PersistenceContext(unitName = "IS1")
    private EntityManager em;

    public Optional<Vehicle> findById(long id) {
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }

    public List<Vehicle> findAll() {
        return em.createQuery("SELECT v FROM Vehicle v ORDER BY v.id", Vehicle.class)
                .getResultList();
    }

    public List<Vehicle> findPage(String filterField, String filterValue, String sort, boolean asc, int page, int size) {
        if (!SORT_FIELDS.contains(sort)) throw new IllegalArgumentException("Нельзя сортировать по полю " + sort);
        String jpql = "SELECT v FROM Vehicle v" + where(filterField, filterValue)
                + " ORDER BY v." + sort + (asc ? " ASC" : " DESC") + ", v.id";
        TypedQuery<Vehicle> q = em.createQuery(jpql, Vehicle.class);
        setFilter(q, filterField, filterValue);
        return q.setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long count(String filterField, String filterValue) {
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(v) FROM Vehicle v" + where(filterField, filterValue), Long.class);
        setFilter(q, filterField, filterValue);
        return q.getSingleResult();
    }

    private String where(String field, String value) {
        if (value == null || value.isBlank()) return "";
        return switch (field) {
            case "name" -> " WHERE LOWER(v.name) LIKE :value";
            case "type", "fuelType" -> matchingEnums(field, value).isEmpty()
                    ? " WHERE v.id IS NULL"
                    : " WHERE v." + field + " IN :value";
            default -> throw new IllegalArgumentException("Нельзя фильтровать по полю " + field);
        };
    }

    private void setFilter(TypedQuery<?> q, String field, String value) {
        if (value == null || value.isBlank()) return;
        if (field.equals("name")) {
            q.setParameter("value", "%" + value.trim().toLowerCase() + "%");
        } else if (!matchingEnums(field, value).isEmpty()) {
            q.setParameter("value", matchingEnums(field, value));
        }
    }

    private List<? extends Enum<?>> matchingEnums(String field, String value) {
        Enum<?>[] all = field.equals("type") ? VehicleType.values() : FuelType.values();
        String v = value.trim().toLowerCase();
        return Arrays.stream(all).filter(e -> e.name().toLowerCase().contains(v)).toList();
    }

    public Vehicle save(Vehicle vehicle) {
        em.persist(vehicle);
        em.flush();
        return vehicle;
    }

    public void delete(Vehicle vehicle) {
        em.remove(vehicle);
    }

    public List<Vehicle> findByFuelType(FuelType fuelType) {
        return em.createQuery("SELECT v FROM Vehicle v WHERE v.fuelType = :f", Vehicle.class)
                .setParameter("f", fuelType).getResultList();
    }

    public List<Vehicle> findByFuelTypeIn(List<FuelType> fuelTypes) {
        return em.createQuery("SELECT v FROM Vehicle v WHERE v.fuelType IN :f ORDER BY v.id", Vehicle.class)
                .setParameter("f", fuelTypes).getResultList();
    }

    public Optional<Vehicle> findOldest() {
        return em.createQuery("SELECT v FROM Vehicle v ORDER BY v.creationDate ASC, v.id ASC", Vehicle.class)
                .setMaxResults(1).getResultStream().findFirst();
    }

    public List<Vehicle> findByEnginePowerBetween(int min, int max) {
        return em.createQuery("SELECT v FROM Vehicle v WHERE v.enginePower BETWEEN :min AND :max ORDER BY v.enginePower", Vehicle.class)
                .setParameter("min", min).setParameter("max", max).getResultList();
    }

    public List<Vehicle> findByCoordinatesId(int coordinatesId) {
        return em.createQuery("SELECT v FROM Vehicle v WHERE v.coordinates.id = :id", Vehicle.class)
                .setParameter("id", coordinatesId).getResultList();
    }
}