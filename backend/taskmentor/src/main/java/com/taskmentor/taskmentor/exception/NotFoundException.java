package com.taskmentor.taskmentor.exception;

/** Maps to HTTP 404, the requested resource does not exist (or is hidden from the caller). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
