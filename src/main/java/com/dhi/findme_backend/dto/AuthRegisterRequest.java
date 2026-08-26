package com.dhi.findme_backend.dto;

import com.dhi.findme_backend.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
    @NotBlank(message = "Le prénom est requis")
    @Size(min = 2, message = "Le prénom doit contenir au moins 2 caractères")
    String firstName,

    @NotBlank(message = "Le nom est requis")
    @Size(min = 2, message = "Le nom doit contenir au moins 2 caractères")
    String lastName,

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    String email,

    @NotBlank(message = "Le mot de passe est requis")
    @ValidPassword
    String password
) {}
