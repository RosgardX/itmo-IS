package com.rosgard.service;

import com.rosgard.entity.FuelType;
import com.rosgard.entity.Vehicle;
import com.rosgard.repository.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Transactional
public class SpecialOperationsService {

    @Inject VehicleRepository vehicles;
    @Inject Event<Vehicle> changed;

    public int deleteByFuelType(FuelType fuelType) {
        List<Vehicle> found = vehicles.findByFuelType(fuelType);
        found.forEach(vehicles::delete);
        if (!found.isEmpty()) changed.fire(found.get(0));
        return found.size();
    }

    public Vehicle oldest() {
        return vehicles.findOldest()
                .orElseThrow(() -> new EntityNotFoundException("В системе нет ни одного объекта"));
    }

    public List<Vehicle> fuelTypeGreaterThan(FuelType fuelType) {
        List<FuelType> greater = Arrays.stream(FuelType.values())
                .filter(f -> f.ordinal() > fuelType.ordinal())
                .toList();
        if (greater.isEmpty()) return List.of();
        return vehicles.findByFuelTypeIn(greater);
    }

    public List<Vehicle> enginePowerBetween(int min, int max) {
        if (min > max) throw new IllegalArgumentException("Минимум не может быть больше максимума");
        return vehicles.findByEnginePowerBetween(min, max);
    }

    public Vehicle addWheels(long id, int count) {
        if (count <= 0) throw new IllegalArgumentException("Число добавляемых колёс должно быть больше 0");
        Vehicle v = vehicles.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle с id " + id + " не найден"));
        if (count > Integer.MAX_VALUE - v.getNumberOfWheels())
            throw new IllegalArgumentException("Слишком большое число колёс");
        v.setNumberOfWheels(v.getNumberOfWheels() + count);
        changed.fire(v);
        return v;
    }
}