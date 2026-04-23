package com.smartcampus.smartcampusapi.dao;

import com.smartcampus.smartcampusapi.model.Sensor;
import java.util.List;
import java.util.stream.Collectors;

public class SensorDAO extends GenericDAO<Sensor> {

    // Single shared instance
    private static final SensorDAO INSTANCE = new SensorDAO();

    // Private constructor
    private SensorDAO() {}

    // Returns the single shared SensorDAO instance
    public static SensorDAO getInstance() {
        return INSTANCE;
    }

    public List<Sensor> findByType(String type) {
        return store.values().stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // Find all sensors assigned to a specific room 
    public List<Sensor> findByRoomId(String roomId) {
        return store.values().stream()
                .filter(s -> roomId.equals(s.getRoomId()))
                .collect(Collectors.toList());
    }
}