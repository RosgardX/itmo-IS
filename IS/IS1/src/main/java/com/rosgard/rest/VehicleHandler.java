package com.rosgard.rest;

import com.rosgard.entity.Vehicle;
import com.rosgard.service.VehicleService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleHandler {

    @Inject
    VehicleService service;

    @GET
    public Map<String, Object> getPage(
            @QueryParam("offset") @DefaultValue("0") @Min(value = 0, message = "offset не может быть отрицательным") int offset,
            @QueryParam("size") @DefaultValue("10") @Min(value = 1, message = "size должен быть от 1 до 100")
            @Max(value = 100, message = "size должен быть от 1 до 100") int size,
            @QueryParam("sort") @DefaultValue("id") String sort,
            @QueryParam("asc") @DefaultValue("true") boolean asc,
            @QueryParam("filterField") @DefaultValue("name") String filterField,
            @QueryParam("filterValue") @DefaultValue("") String filterValue) {
        return service.getPage(filterField, filterValue, sort, asc, offset, size);
    }

    @GET
    @Path("/{id}")
    public Vehicle getById(@PathParam("id") long id) {
        return service.getById(id);
    }

    @POST
    public Vehicle create(Vehicle vehicle) {
        return service.create(vehicle);
    }

    @PUT
    @Path("/{id}")
    public Vehicle update(@PathParam("id") long id, Vehicle vehicle) {
        return service.update(id, vehicle);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") long id) {
        service.delete(id);
    }
}
