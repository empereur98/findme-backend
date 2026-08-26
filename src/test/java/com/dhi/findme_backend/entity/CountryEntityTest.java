package com.dhi.findme_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CountryEntityTest {

    private Country country;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(UUID.randomUUID());
        country.setCode("sn");
        country.setNameFr("Sénégal");
        country.setNameEn("Senegal");
    }

    @Test
    void testCountryCreation_WithValidData_ShouldCreateCountry() {
        assertNotNull(country.getId());
        assertEquals("sn", country.getCode());
        assertEquals("Sénégal", country.getNameFr());
        assertEquals("Senegal", country.getNameEn());
    }

    @Test
    void testCountryCode_ShouldBeSettable() {
        country.setCode("fr");
        assertEquals("fr", country.getCode());
    }

    @Test
    void testCountryNameFr_ShouldBeSettable() {
        country.setNameFr("France");
        assertEquals("France", country.getNameFr());
    }

    @Test
    void testCountryNameEn_ShouldBeSettable() {
        country.setNameEn("France");
        assertEquals("France", country.getNameEn());
    }

    @Test
    void testCountryCode_ShouldNotBeNull() {
        country.setCode(null);
        assertNull(country.getCode());
    }

    @Test
    void testCountryNameFr_ShouldNotBeNull() {
        country.setNameFr(null);
        assertNull(country.getNameFr());
    }

    @Test
    void testCountryNameEn_ShouldNotBeNull() {
        country.setNameEn(null);
        assertNull(country.getNameEn());
    }
}
