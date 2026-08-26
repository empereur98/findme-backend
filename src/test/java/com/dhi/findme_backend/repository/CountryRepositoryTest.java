package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryRepositoryTest {

    @Mock
    private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setId(UUID.randomUUID());
        testCountry.setCode("sn");
        testCountry.setNameFr("Sénégal");
        testCountry.setNameEn("Senegal");
    }

    @Test
    void testFindByCode_WhenCountryExists_ShouldReturnCountry() {
        when(countryRepository.findByCode("sn")).thenReturn(Optional.of(testCountry));

        Optional<Country> foundCountry = countryRepository.findByCode("sn");

        assertTrue(foundCountry.isPresent());
        assertEquals("sn", foundCountry.get().getCode());
        assertEquals("Sénégal", foundCountry.get().getNameFr());
        assertEquals("Senegal", foundCountry.get().getNameEn());
        verify(countryRepository).findByCode("sn");
    }

    @Test
    void testFindByCode_WhenCountryNotExists_ShouldReturnEmpty() {
        when(countryRepository.findByCode("xx")).thenReturn(Optional.empty());

        Optional<Country> foundCountry = countryRepository.findByCode("xx");

        assertFalse(foundCountry.isPresent());
        verify(countryRepository).findByCode("xx");
    }

    @Test
    void testSave_WithValidCountry_ShouldPersistCountry() {
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        Country savedCountry = countryRepository.save(testCountry);

        assertNotNull(savedCountry);
        assertNotNull(savedCountry.getId());
        assertEquals("sn", savedCountry.getCode());
        verify(countryRepository).save(testCountry);
    }

    @Test
    void testDelete_WithExistingCountry_ShouldRemoveCountry() {
        doNothing().when(countryRepository).delete(testCountry);

        countryRepository.delete(testCountry);

        verify(countryRepository).delete(testCountry);
    }

    @Test
    void testFindById_WithExistingCountry_ShouldReturnCountry() {
        UUID countryId = testCountry.getId();
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(testCountry));

        Optional<Country> foundCountry = countryRepository.findById(countryId);

        assertTrue(foundCountry.isPresent());
        assertEquals(countryId, foundCountry.get().getId());
        verify(countryRepository).findById(countryId);
    }

    @Test
    void testFindById_WithNonExistingCountry_ShouldReturnEmpty() {
        UUID randomId = UUID.randomUUID();
        when(countryRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<Country> foundCountry = countryRepository.findById(randomId);

        assertFalse(foundCountry.isPresent());
        verify(countryRepository).findById(randomId);
    }

    @Test
    void testFindAll_WithMultipleCountries_ShouldReturnAll() {
        when(countryRepository.findAll()).thenReturn(java.util.List.of(testCountry));

        var countries = countryRepository.findAll();

        assertEquals(1, countries.size());
        verify(countryRepository).findAll();
    }

    @Test
    void testCount_WithMultipleCountries_ShouldReturnCorrectCount() {
        when(countryRepository.count()).thenReturn(2L);

        long count = countryRepository.count();

        assertEquals(2, count);
        verify(countryRepository).count();
    }
}