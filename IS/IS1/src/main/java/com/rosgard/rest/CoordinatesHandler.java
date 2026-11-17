package com.rosgard.rest;

import com.rosgard.service.CoordinatesService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
public class CoordinatesHandler {

    @Inject
    CoordinatesService service;

    @GET
    public Map<String, Object> getPage(
            @QueryParam("offset") @DefaultValue("0") @Min(value = 0, message = "offset не может быть отрицательным") int offset,
            @QueryParam("size") @DefaultValue("20") @Min(value = 1, message = "size должен быть от 1 до 100")
            @Max(value = 100, message = "size должен быть от 1 до 100") int size) {
        return service.getPage(offset, size);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") int id, @QueryParam("reassignTo") Integer reassignTo) {
        service.deleteAndReassign(id, reassignTo);
    }
}
