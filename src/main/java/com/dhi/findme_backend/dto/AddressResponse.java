package com.dhi.findme_backend.dto;

public record AddressResponse(
    String id,
    String code,
    String name,
    String country,
    String city,
    String district,
    String street,
    String landmark,
    GpsCoordinates gps,
    String imageFacade,
    String codePlus,
    String ownerName,
    String ownerEmail,
    String status,
    String date,
    String type
) {}
