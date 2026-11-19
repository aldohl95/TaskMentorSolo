package com.taskmentor.taskmentor.exception;

/** Maps to HTTP 409, the request conflicts with the current state of a resource. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
