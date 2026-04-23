package com.smartcampus.smartcampusapi.model;

import java.util.UUID;

public class SensorReading {
    private String id;  // Unique reading ID
    private long timestamp;  // Epoch time in milliseconds when reading was captured
    private double value;   // Actual metric value recorded by the sensor hardware

     // Default constructor
    public SensorReading() {}

    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}