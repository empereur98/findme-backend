package com.dhi.findme_backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String message,
        String type,
        Boolean read,
        String actionUrl,
        LocalDateTime createdAt
) {
}
