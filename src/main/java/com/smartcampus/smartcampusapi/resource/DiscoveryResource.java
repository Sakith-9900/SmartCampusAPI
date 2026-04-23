package com.smartcampus.smartcampusapi.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {
    
    // UriInfo injected by JAX-RS
    @Context
    private UriInfo uriInfo;

    // Returns API version, title, admin contact and resource collection links.
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        // Get the base URI dynamically
        String base = uriInfo.getBaseUri().toString();

        Map<String, Object> info = new HashMap<>();
        info.put("apiVersion", "1.0");
        info.put("title", "Smart Campus Sensor & Room Management API");
        info.put("description", "A RESTful API to manage university campus rooms and sensors");
        info.put("adminContact", "admin@smartcampus.ac.uk");

         // Collections map
        Map<String, String> collections = new HashMap<>();
        collections.put("rooms",   base + "rooms");
        collections.put("sensors", base + "sensors");
        info.put("collections", collections);

        return Response.ok(info).build();
    }
}