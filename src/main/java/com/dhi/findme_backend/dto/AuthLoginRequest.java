package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    String email,

    @NotBlank(message = "Le mot de passe est requis")
    String password
) {}
