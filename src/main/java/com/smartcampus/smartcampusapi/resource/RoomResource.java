package com.smartcampus.smartcampusapi.resource;

import com.smartcampus.smartcampusapi.dao.RoomDAO;
import com.smartcampus.smartcampusapi.exception.RoomNotEmptyException;
import com.smartcampus.smartcampusapi.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

// Handles all CRUD operations for /api/v1/rooms
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // DAO instance for room data access
    private final RoomDAO roomDAO = RoomDAO.getInstance();

    // GET /api/v1/rooms - list all rooms
    @GET
    public Response getAllRooms() {
        List<Room> rooms = roomDAO.findAll();
        return Response.ok(rooms).build();
    }

    // POST /api/v1/rooms - create a room
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }
        if (roomDAO.existsById(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Room with this ID already exists\"}")
                    .build();
        }
        roomDAO.save(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // GET /api/v1/rooms/{roomId} - get specific room
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        return Response.ok(room).build();
    }


    // DELETE /api/v1/rooms/{roomId} - delete room, blocked if sensors assigned
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = roomDAO.findById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        //  409 Conflict if room still has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room '" + roomId + "' cannot be deleted. It still has "
                + room.getSensorIds().size() + " active sensor(s) assigned."
            );
        }
        roomDAO.deleteById(roomId);
        return Response.noContent().build();
    }
}
