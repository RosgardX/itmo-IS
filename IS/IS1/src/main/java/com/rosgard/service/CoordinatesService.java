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

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Transactional
public class CoordinatesService {

    @Inject CoordinatesRepository coordinates;
    @Inject VehicleRepository vehicles;
    @Inject Event<Vehicle> changed;

    public List<Coordinates> getAll() {
        return coordinates.findAll();
    }

    public List<Map<String, Object>> getAllWithUsage() {
        return coordinates.findAllWithUsage().stream()
                .map(r -> Map.of("id", r[0], "x", r[1], "y", r[2], "vehicles", r[3]))
                .toList();
    }

    public void deleteAndReassign(int id, Integer reassignToId) {
        Coordinates victim = find(id);
        List<Vehicle> linked = vehicles.findByCoordinatesId(id);

        if (!linked.isEmpty()) {
            if (reassignToId == null)
                throw new IllegalArgumentException("К координатам #" + id + " привязано машин: " + linked.size()
                        + ". Выберите, к каким координатам их перепривязать");
            if (reassignToId == id)
                throw new IllegalArgumentException("Нельзя перепривязать машины к тем же координатам, которые удаляются");
            Coordinates target = find(reassignToId);
            for (Vehicle v : linked) v.setCoordinates(target);
        }

        coordinates.delete(victim);
        if (!linked.isEmpty()) changed.fire(linked.get(0));
    }

    private Coordinates find(int id) {
        return coordinates.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Координаты с id " + id + " не найдены"));
    }
}