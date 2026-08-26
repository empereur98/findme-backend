package com.dhi.findme_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CampayCollectRequest(
        @NotNull(message = "Le montant est requis")
        @Positive(message = "Le montant doit être positif")
        @JsonProperty("amount")
        BigDecimal amount,

        @NotBlank(message = "La devise est requise")
        @JsonProperty("currency")
        String currency,

        @NotBlank(message = "Le numéro de téléphone est requis")
        @JsonProperty("from")
        String phoneNumber,

        @NotBlank(message = "La description est requise")
        @JsonProperty("description")
        String description,

        @NotBlank(message = "La référence externe est requise")
        @JsonProperty("external_reference")
        String externalReference
) {
}
