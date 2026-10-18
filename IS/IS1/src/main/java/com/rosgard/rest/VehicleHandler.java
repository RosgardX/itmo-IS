package com.rosgard.rest;

import com.rosgard.entity.Vehicle;
import com.rosgard.service.VehicleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON) // отвечаем JSON
@Consumes(MediaType.APPLICATION_JSON)

@Path("/vehicles")
public class VehicleHandler {

    @Inject VehicleService service;

    @GET
    public Map<String, Object> getVehicles(@QueryParam("page") @DefaultValue("0") int page,
                                           @QueryParam("size") @DefaultValue("10") int size,
                                           @QueryParam("sort") @DefaultValue("id") String sort,
                                           @QueryParam("asc") @DefaultValue("true") boolean asc,
                                           @QueryParam("filterField") @DefaultValue("name") String filterField,
                                           @QueryParam("filterValue") @DefaultValue("") String filterValue) {
        return service.getPage(filterField, filterValue, sort, asc, page, size);
    }

    @GET
    @Path("/{id}")
    public Vehicle getById(@PathParam("id") int id) {return service.getById(id);}

    @POST
    public Vehicle create(Vehicle vehicle) {return service.create(vehicle);}

    @PUT
    @Path("/{id}")
    public Vehicle update(@PathParam("id") int id, Vehicle vehicle) {return service.update(id, vehicle);}

    @DELETE
    @Path("/{id}")
    public void delete (@PathParam("id") int id) {service.delete(id);}
}
