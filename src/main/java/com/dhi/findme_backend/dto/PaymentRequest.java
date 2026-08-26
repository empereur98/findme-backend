package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Le montant est requis")
        @Positive(message = "Le montant doit être positif")
        BigDecimal amount,
        
        @NotBlank(message = "La devise est requise")
        String currency,
        
        @NotBlank(message = "L'email est requis")
        String email,
        
        @NotBlank(message = "Le numéro de téléphone est requis")
        String phoneNumber,
        
        @NotBlank(message = "Le nom est requis")
        String name,
        
        @NotBlank(message = "La référence de transaction est requise")
        String txRef,
        
        String redirectUrl,
        
        String paymentMethod
) {
}
