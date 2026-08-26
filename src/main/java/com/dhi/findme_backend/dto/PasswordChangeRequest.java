package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank(message = "L'ancien mot de passe est requis")
        String oldPassword,
        
        @NotBlank(message = "Le nouveau mot de passe est requis")
        @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caractères")
        String newPassword
) {
}
