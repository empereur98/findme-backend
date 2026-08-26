package com.dhi.findme_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SubscriptionUpgradeRequest(
    @NotBlank(message = "Le moyen de paiement est requis")
    String paymentMethod,

    String phone,

    @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Le code pays doit être au format +XXX")
    String countryCode,

    @NotBlank(message = "Le plan est requis")
    String plan
) {}
