package com.taskmentor.taskmentor.dto;

/** The saved resource plus whether it was newly created (HTTP 201) or updated (HTTP 200). */
public record SaveResult<T>(T body, boolean created) {
}
