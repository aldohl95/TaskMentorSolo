package com.taskmentor.taskmentor.exception;

/** Maps to HTTP 400, the request is well-formed but semantically invalid. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
