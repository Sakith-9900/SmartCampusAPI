package com.smartcampus.smartcampusapi.dao;

import com.smartcampus.smartcampusapi.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SensorReadingDAO {

    // Single shared instance
    private static final SensorReadingDAO INSTANCE = new SensorReadingDAO();

    private final Map<String, List<SensorReading>> readingsStore = new ConcurrentHashMap<>();

    // Private constructor
    private SensorReadingDAO() {}

    // Returns the single shared SensorReadingDAO instance
    public static SensorReadingDAO getInstance() {
        return INSTANCE;
    }

    // Add a new reading to a sensor's history.
    public void addReading(String sensorId, SensorReading reading) {
        readingsStore.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }

    // Get all historical readings for a senso
    public List<SensorReading> getReadings(String sensorId) {
        return readingsStore.getOrDefault(sensorId, new ArrayList<>());
    }
}