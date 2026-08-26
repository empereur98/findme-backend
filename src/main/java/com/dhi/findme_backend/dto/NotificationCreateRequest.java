package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationCreateRequest(
        @NotBlank(message = "Le titre est requis")
        String title,
        
        @NotBlank(message = "Le message est requis")
        String message,
        
        @NotBlank(message = "Le type est requis")
        String type,
        
        String actionUrl
) {
}
