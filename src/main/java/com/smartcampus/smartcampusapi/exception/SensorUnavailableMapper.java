package com.smartcampus.smartcampusapi.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

// Maps to HTTP 403 Forbidden when a reading is posted to a MAINTENANCE sensor.
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 403);
        error.put("error", "Forbidden");
        error.put("message", e.getMessage());
        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}