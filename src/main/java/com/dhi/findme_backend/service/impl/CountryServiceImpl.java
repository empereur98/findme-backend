package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.CountryResponse;
import com.dhi.findme_backend.entity.Country;
import com.dhi.findme_backend.mapper.CountryMapper;
import com.dhi.findme_backend.repository.CountryRepository;
import com.dhi.findme_backend.service.CountryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryServiceImpl(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    @Override
    public List<CountryResponse> getAllCountries() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream()
                .map(countryMapper::toCountryResponse)
                .toList();
    }
}
