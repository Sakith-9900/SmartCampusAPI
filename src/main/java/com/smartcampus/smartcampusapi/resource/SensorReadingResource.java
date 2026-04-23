package com.smartcampus.smartcampusapi.resource;

import com.smartcampus.smartcampusapi.dao.SensorDAO;
import com.smartcampus.smartcampusapi.dao.SensorReadingDAO;
import com.smartcampus.smartcampusapi.exception.SensorUnavailableException;
import com.smartcampus.smartcampusapi.model.Sensor;
import com.smartcampus.smartcampusapi.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

 // Handles GET and POST for /api/v1/sensors/{sensorId}/readings
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final SensorDAO sensorDAO = SensorDAO.getInstance();
    private final SensorReadingDAO readingDAO = SensorReadingDAO.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings - get all readings history
    @GET
    public Response getReadings() {
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        List<SensorReading> readings = readingDAO.getReadings(sensorId);
        return Response.ok(readings).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings - add new reading
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = sensorDAO.findById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        // 403 Forbidden if MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is under MAINTENANCE and cannot accept new readings."
            );
        }
        // Also block OFFLINE sensors
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\":\"Sensor '" + sensorId + "' is OFFLINE and cannot accept readings.\"}")
                    .build();
        }

        SensorReading newReading = new SensorReading(reading.getValue());
        readingDAO.addReading(sensorId, newReading);

        // update currentValue on parent sensor
        sensor.setCurrentValue(reading.getValue());
        sensorDAO.save(sensorId, sensor);

        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}
