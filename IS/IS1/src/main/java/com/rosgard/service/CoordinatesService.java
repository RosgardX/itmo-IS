package com.rosgard.service;

import com.rosgard.entity.Coordinates;
import com.rosgard.entity.Vehicle;
import com.rosgard.exception.InvalidRequestException;
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

    @Inject
    CoordinatesRepository coordinates;
    @Inject
    VehicleRepository vehicles;
    @Inject
    Event<Vehicle> changed;

    public Map<String, Object> getPage(int offset, int size) {
        List<Coordinates> page = coordinates.findPage(offset, size);
        Map<Integer, Long> usage = vehicles.countByCoordinates(page.stream().map(Coordinates::getId).toList());
        List<Map<String, Object>> items = page.stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "x", c.getX(), "y", c.getY(),
                        "vehicles", usage.getOrDefault(c.getId(), 0L)))
                .toList();
        return Map.of("items", items, "total", coordinates.count());
    }

    public void deleteAndReassign(int id, Integer reassignToId) {
        Coordinates victim = find(id);
        List<Vehicle> linked = vehicles.findByCoordinatesId(id);

        if (!linked.isEmpty()) {
            if (reassignToId == null)
                throw new InvalidRequestException("К координатам #" + id + " привязано машин: " + linked.size()
                        + ". Укажите ID координат, к которым их перепривязать");
            if (reassignToId == id)
                throw new InvalidRequestException("Нельзя перепривязать машины к тем же координатам, которые удаляются");
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