package com.smartcampus.smartcampusapi.exception;

// Custom exception thrown when attempting to delete a Room
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}