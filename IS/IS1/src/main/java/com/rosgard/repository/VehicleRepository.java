package com.rosgard.repository;

import com.rosgard.entity.FuelType;
import com.rosgard.entity.Vehicle;
import com.rosgard.entity.VehicleType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehicle> q = cb.createQuery(Vehicle.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.orderBy(cb.asc(v.get("id")));
        return em.createQuery(q).getResultList();
    }

    public List<Vehicle> findPage(String filterField, String filterValue, String sort, boolean asc, int page, int size) {
        if (!SORT_FIELDS.contains(sort)) throw new IllegalArgumentException("Нельзя сортировать по полю " + sort);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehicle> q = cb.createQuery(Vehicle.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.where(filter(cb, v, filterField, filterValue));
        Path<?> sortPath = path(v, sort);
        q.orderBy(asc ? cb.asc(sortPath) : cb.desc(sortPath), cb.asc(v.get("id")));
        return em.createQuery(q)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long count(String filterField, String filterValue) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.select(cb.count(v)).where(filter(cb, v, filterField, filterValue));
        return em.createQuery(q).getSingleResult();
    }

    private Predicate filter(CriteriaBuilder cb, Root<Vehicle> v, String field, String value) {
        if (value == null || value.isBlank()) return cb.conjunction();
        String text = value.trim().toLowerCase();
        return switch (field) {
            case "name" -> cb.like(cb.lower(v.get("name")), "%" + text + "%");
            case "type" -> in(cb, v.get("type"), matching(VehicleType.values(), text));
            case "fuelType" -> in(cb, v.get("fuelType"), matching(FuelType.values(), text));
            default -> throw new IllegalArgumentException("Нельзя фильтровать по полю " + field);
        };
    }

    private Predicate in(CriteriaBuilder cb, Path<?> field, List<? extends Enum<?>> values) {
        return values.isEmpty() ? cb.disjunction() : field.in(values);
    }

    private List<? extends Enum<?>> matching(Enum<?>[] all, String text) {
        return Arrays.stream(all).filter(e -> e.name().toLowerCase().contains(text)).toList();
    }

    private Path<?> path(Root<Vehicle> v, String field) {
        Path<?> p = v;
        for (String part : field.split("\\.")) p = p.get(part);
        return p;
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehicle> q = cb.createQuery(Vehicle.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.where(cb.equal(v.get("fuelType"), fuelType));
        return em.createQuery(q).getResultList();
    }

    public List<Vehicle> findByFuelTypeIn(List<FuelType> fuelTypes) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehicle> q = cb.createQuery(Vehicle.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.where(v.get("fuelType").in(fuelTypes)).orderBy(cb.asc(v.get("id")));
        return em.createQuery(q).getResultList();
    }

    public Optional<Vehicle> findOldest() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehicle> q = cb.createQuery(Vehicle.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.orderBy(cb.asc(v.get("creationDate")), cb.asc(v.get("id")));
        return em.createQuery(q).setMaxResults(1).getResultStream().findFirst();
    }

    public List<Vehicle> findByEnginePowerBetween(int min, int max) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehicle> q = cb.createQuery(Vehicle.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.where(cb.between(v.get("enginePower"), min, max)).orderBy(cb.asc(v.get("enginePower")));
        return em.createQuery(q).getResultList();
    }

    public List<Vehicle> findByCoordinatesId(int coordinatesId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehicle> q = cb.createQuery(Vehicle.class);
        Root<Vehicle> v = q.from(Vehicle.class);
        q.where(cb.equal(v.get("coordinates").get("id"), coordinatesId));
        return em.createQuery(q).getResultList();
    }

    public Map<Integer, Long> countByCoordinates() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<Vehicle> v = q.from(Vehicle.class);
        Path<Integer> id = v.get("coordinates").get("id");
        q.multiselect(id, cb.count(v)).groupBy(id);
        return em.createQuery(q).getResultList().stream()
                .collect(Collectors.toMap(t -> t.get(0, Integer.class), t -> t.get(1, Long.class)));
    }
}
