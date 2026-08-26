package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpResetPasswordRequest(
    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    String email,
    
    @NotBlank(message = "Le code OTP est requis")
    @Size(min = 6, max = 6, message = "Le code OTP doit contenir 6 chiffres")
    String otp,
    
    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    String newPassword
) {}
