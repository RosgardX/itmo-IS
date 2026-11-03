package com.rosgard.service;

import com.rosgard.entity.Coordinates;
import com.rosgard.entity.Vehicle;
import com.rosgard.repository.CoordinatesRepository;
import com.rosgard.repository.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
@Transactional
public class VehicleService {

    @Inject
    VehicleRepository vehicles;
    @Inject
    CoordinatesRepository coordinates;
    @Inject
    Validator validator;
    @Inject
    Event<Vehicle> changed;

    public List<Vehicle> getAll() {
        return vehicles.findAll();
    }

    public Map<String, Object> getPage(String filterField, String filterValue, String sort, boolean asc, int page, int size) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("Неверные параметры страницы");
        List<Vehicle> items = vehicles.findPage(filterField, filterValue, sort, asc, page, size);
        long total = vehicles.count(filterField, filterValue);
        return Map.of("items", items, "total", total);
    }

    public Vehicle getById(long id) {
        return vehicles.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle с id " + id + " не найден"));
    }

    public Vehicle create(Vehicle v) {
        v.setId(0);
        v.setCoordinates(resolveCoordinates(v.getCoordinates()));
        validate(v);
        Vehicle saved = vehicles.save(v);
        changed.fire(saved);
        return saved;
    }

    public Vehicle update(long id, Vehicle data) {
        Vehicle v = getById(id);
        v.setName(data.getName());
        v.setCoordinates(resolveCoordinates(data.getCoordinates()));
        v.setType(data.getType());
        v.setEnginePower(data.getEnginePower());
        v.setNumberOfWheels(data.getNumberOfWheels());
        v.setCapacity(data.getCapacity());
        v.setDistanceTravelled(data.getDistanceTravelled());
        v.setFuelConsumption(data.getFuelConsumption());
        v.setFuelType(data.getFuelType());
        validate(v);
        changed.fire(v);
        return v;
    }

    public void delete(long id) {
        Vehicle v = getById(id);
        vehicles.delete(v);
        changed.fire(v);
    }

    private Coordinates resolveCoordinates(Coordinates c) {
        if (c == null || c.getId() == 0) return c;
        return coordinates.findById(c.getId())
                .orElseThrow(() -> new EntityNotFoundException("Координаты с id " + c.getId() + " не найдены"));
    }

    private <T> void validate(T obj) {
        Set<ConstraintViolation<T>> errors = validator.validate(obj);
        if (!errors.isEmpty()) throw new ConstraintViolationException(errors);
    }
}