package com.dhi.findme_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(
    LocalDateTime timestamp,
    int status,
    String message,
    T data,
    String path
) {
    public StandardResponse(int status, String message, T data, String path) {
        this(LocalDateTime.now(), status, message, data, path);
    }
    
    public static <T> StandardResponse<T> success(int status, String message, T data, String path) {
        return new StandardResponse<>(status, message, data, path);
    }
    
    public static <T> StandardResponse<T> success(int status, String message, String path) {
        return new StandardResponse<>(status, message, null, path);
    }
}
