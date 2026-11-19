package com.taskmentor.taskmentor.exception;

/** Maps to HTTP 403, the caller is authenticated but not allowed to do this. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
