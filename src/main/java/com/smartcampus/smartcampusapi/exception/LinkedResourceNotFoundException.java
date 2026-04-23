package com.smartcampus.smartcampusapi.exception;

// Custom exception thrown when a sensor is created with a roomId

public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}