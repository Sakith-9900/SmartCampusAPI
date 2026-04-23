package com.smartcampus.smartcampusapi.dao;

import com.smartcampus.smartcampusapi.model.Room;

public class RoomDAO extends GenericDAO<Room> {

    // Single shared instance
    private static final RoomDAO INSTANCE = new RoomDAO();

    // Private constructor
    private RoomDAO() {}

    // Returns the single shared RoomDAO instance
    public static RoomDAO getInstance() {
        return INSTANCE;
    }
}