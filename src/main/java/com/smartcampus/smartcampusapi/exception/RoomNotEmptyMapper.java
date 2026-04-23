package com.smartcampus.smartcampusapi.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

// Maps RoomNotEmptyException to HTTP 409 Conflict response.

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 409);
        error.put("error", "Conflict");
        error.put("message", e.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}