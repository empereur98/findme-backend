package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpVerifyRequest(
    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    String email,
    
    @NotBlank(message = "Le code OTP est requis")
    @Size(min = 6, max = 6, message = "Le code OTP doit contenir 6 chiffres")
    String code
) {}
