package com.smartcampus.smartcampusapi.exception;

// Custom exception thrown when a POST reading is attempted
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}