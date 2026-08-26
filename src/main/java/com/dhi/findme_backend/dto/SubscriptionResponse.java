package com.dhi.findme_backend.dto;

public record SubscriptionResponse(
    String plan,
    Integer maxAddresses,
    Integer addressesCreatedCount,
    Features features
) {}
