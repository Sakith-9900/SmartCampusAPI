package com.smartcampus.smartcampusapi.resource;

import com.smartcampus.smartcampusapi.dao.RoomDAO;
import com.smartcampus.smartcampusapi.dao.SensorDAO;
import com.smartcampus.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.smartcampus.smartcampusapi.model.Room;
import com.smartcampus.smartcampusapi.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// Handles all CRUD operations for /api/v1/sensors
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // DAO instances
    private final SensorDAO sensorDAO = SensorDAO.getInstance();
    private final RoomDAO roomDAO = RoomDAO.getInstance();

    // GET /api/v1/sensors  or  GET /api/v1/sensors?type=CO2
    @GET
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result;
        if (type != null && !type.isEmpty()) {
            result = sensorDAO.findByType(type);
        } else {
            result = sensorDAO.findAll();
        }
        return Response.ok(result).build();
    }

    // POST /api/v1/sensors - create sensor, validate roomId exists
   @POST
   public Response createSensor(Sensor sensor) {
    // 1. Check sensor ID first
    if (sensor.getId() == null || sensor.getId().isEmpty())
        return Response.status(400)
                .entity("{\"error\":\"Sensor ID is required\"}")
                .build();

    // 2. Check for duplicate sensor ID
    if (sensorDAO.existsById(sensor.getId()))
        return Response.status(409)
                .entity("{\"error\":\"Sensor already exists\"}")
                .build();

    // 3. Check roomId exists (422 if not)
    if (sensor.getRoomId() == null || !roomDAO.existsById(sensor.getRoomId()))
        throw new LinkedResourceNotFoundException(
            "Room with ID '" + sensor.getRoomId() + "' does not exist.");

    // 4. Link and save
    Room room = roomDAO.findById(sensor.getRoomId());
    room.getSensorIds().add(sensor.getId());
    sensorDAO.save(sensor.getId(), sensor);
    return Response.status(201).entity(sensor).build();
}

    // GET /api/v1/sensors/{sensorId}
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        return Response.ok(sensor).build();
    }


    // DELETE /api/v1/sensors/{sensorId}
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        // Unlink from room
        Room room = roomDAO.findById(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }
        sensorDAO.deleteById(sensorId);
        return Response.noContent().build();
    }

    // Sub-resource locator — Part 4
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
