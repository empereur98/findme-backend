package com.dhi.findme_backend.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String errorCode,
    String message,
    String path,
    List<ValidationError> errors
) {
    public ErrorResponse(int status, String errorCode, String message, String path) {
        this(LocalDateTime.now(), status, errorCode, message, path, null);
    }

    public ErrorResponse(int status, String errorCode, String message, String path, List<ValidationError> errors) {
        this(LocalDateTime.now(), status, errorCode, message, path, errors);
    }
}