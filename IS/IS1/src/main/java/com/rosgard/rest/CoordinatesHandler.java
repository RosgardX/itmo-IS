package com.rosgard.rest;

import com.rosgard.entity.Coordinates;
import com.rosgard.service.CoordinatesService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
public class CoordinatesHandler {

    @Inject CoordinatesService service;

    @GET
    public List<Coordinates> getAll() { return service.getAll(); }

    @GET
    @Path("/usage")
    public List<Map<String, Object>> getUsage() { return service.getAllWithUsage(); }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") int id, @QueryParam("reassignTo") Integer reassignTo) {
        service.deleteAndReassign(id, reassignTo);
    }
}