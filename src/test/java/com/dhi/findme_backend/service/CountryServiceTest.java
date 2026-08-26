package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.CountryResponse;
import com.dhi.findme_backend.entity.Country;
import com.dhi.findme_backend.mapper.CountryMapper;
import com.dhi.findme_backend.repository.CountryRepository;
import com.dhi.findme_backend.service.impl.CountryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country senegal;
    private Country france;
    private CountryResponse senegalResponse;
    private CountryResponse franceResponse;

    @BeforeEach
    void setUp() {
        senegal = new Country();
        senegal.setCode("sn");
        senegal.setNameFr("Sénégal");
        senegal.setNameEn("Senegal");

        france = new Country();
        france.setCode("fr");
        france.setNameFr("France");
        france.setNameEn("France");

        senegalResponse = new CountryResponse(
                "sn",
                "Sénégal",
                "Senegal",
                Arrays.asList("Dakar", "Saint-Louis", "Thiès")
        );

        franceResponse = new CountryResponse(
                "fr",
                "France",
                "France",
                Arrays.asList("Paris", "Lyon", "Marseille")
        );
    }

    @Test
    void testGetAllCountries_Success() {
        List<Country> countries = Arrays.asList(senegal, france);
        when(countryRepository.findAll()).thenReturn(countries);
        when(countryMapper.toCountryResponse(senegal)).thenReturn(senegalResponse);
        when(countryMapper.toCountryResponse(france)).thenReturn(franceResponse);

        List<CountryResponse> response = countryService.getAllCountries();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(countryRepository).findAll();
        verify(countryMapper, times(2)).toCountryResponse(any(Country.class));
    }

    @Test
    void testGetAllCountries_EmptyList() {
        when(countryRepository.findAll()).thenReturn(Arrays.asList());

        List<CountryResponse> response = countryService.getAllCountries();

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(countryRepository).findAll();
        verify(countryMapper, never()).toCountryResponse(any(Country.class));
    }

    @Test
    void testGetAllCountries_SingleCountry() {
        List<Country> countries = Arrays.asList(senegal);
        when(countryRepository.findAll()).thenReturn(countries);
        when(countryMapper.toCountryResponse(senegal)).thenReturn(senegalResponse);

        List<CountryResponse> response = countryService.getAllCountries();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Sénégal", response.get(0).nameFr());
        verify(countryRepository).findAll();
        verify(countryMapper).toCountryResponse(senegal);
    }
}
