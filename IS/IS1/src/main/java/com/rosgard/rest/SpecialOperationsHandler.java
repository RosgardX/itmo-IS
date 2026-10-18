package com.rosgard.rest;

import com.rosgard.entity.FuelType;
import com.rosgard.entity.Vehicle;
import com.rosgard.service.SpecialOperationsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/special")
@Produces(MediaType.APPLICATION_JSON)
public class SpecialOperationsHandler {

    @Inject SpecialOperationsService service;

    @DELETE
    @Path("/fuel-type/{fuelType}")
    public Map<String, Integer> deleteByFuelType(@PathParam("fuelType") FuelType fuelType) {
        return Map.of("deleted", service.deleteByFuelType(fuelType));
    }

    @GET
    @Path("/oldest")
    public Vehicle oldest() { return service.oldest(); }

    @GET
    @Path("/fuel-type-greater/{fuelType}")
    public List<Vehicle> fuelTypeGreater(@PathParam("fuelType") FuelType fuelType) {
        return service.fuelTypeGreaterThan(fuelType);
    }

    @GET
    @Path("/engine-power")
    public List<Vehicle> enginePower(@QueryParam("min") int min, @QueryParam("max") int max) {
        return service.enginePowerBetween(min, max);
    }

    @POST
    @Path("/{id}/add-wheels")
    public Vehicle addWheels(@PathParam("id") long id, @QueryParam("count") int count) {
        return service.addWheels(id, count);
    }
}