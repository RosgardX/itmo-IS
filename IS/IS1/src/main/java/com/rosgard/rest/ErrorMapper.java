package com.rosgard.rest;

import com.rosgard.exception.InvalidRequestException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ErrorMapper.class.getName());

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof WebApplicationException w) return w.getResponse();
        if (e instanceof ConstraintViolationException c) {
            return error(Response.Status.BAD_REQUEST, c.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage).toList());
        }
        if (e instanceof InvalidRequestException) return error(Response.Status.BAD_REQUEST, List.of(e.getMessage()));
        if (e instanceof ProcessingException)
            return error(Response.Status.BAD_REQUEST, List.of("Некорректный JSON в запросе"));
        if (e instanceof EntityNotFoundException) return error(Response.Status.NOT_FOUND, List.of(e.getMessage()));
        LOG.log(Level.SEVERE, "Необработанная ошибка", e);
        return error(Response.Status.INTERNAL_SERVER_ERROR, List.of("Внутренняя ошибка сервера"));
    }

    private Response error(Response.Status status, List<String> errors) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("errors", errors))
                .build();
    }
}
