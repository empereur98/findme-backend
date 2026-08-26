package com.dhi.findme_backend.dto.error;

public record ValidationError(
    String field,
    String message
) {}