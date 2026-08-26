package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.CountryResponse;

import java.util.List;

public interface CountryService {

    List<CountryResponse> getAllCountries();
}
