package com.dhi.findme_backend.dto;

import java.util.List;

public record CountryResponse(
    String code,
    String nameFr,
    String nameEn,
    List<String> cities
) {}
