package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.CountryResponse;
import com.dhi.findme_backend.entity.Country;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CountryMapperTest {

    private CountryMapper countryMapper;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        countryMapper = Mappers.getMapper(CountryMapper.class);
        testCountry = new Country();
        testCountry.setCode("sn");
        testCountry.setNameFr("Sénégal");
        testCountry.setNameEn("Senegal");
        testCountry.setCities("Dakar, Saint-Louis, Thiès");
    }

    @Test
    void testToCountryResponse_WithAllFields_ShouldMapCorrectly() {
        CountryResponse response = countryMapper.toCountryResponse(testCountry);

        assertNotNull(response);
        assertEquals(testCountry.getCode(), response.code());
        assertEquals(testCountry.getNameFr(), response.nameFr());
        assertEquals(testCountry.getNameEn(), response.nameEn());
        assertNotNull(response.cities());
        assertEquals(3, response.cities().size());
        assertTrue(response.cities().contains("Dakar"));
        assertTrue(response.cities().contains(" Saint-Louis"));
        assertTrue(response.cities().contains(" Thiès"));
    }

    @Test
    void testToCountryResponse_WithNullCities_ShouldReturnEmptyList() {
        testCountry.setCities(null);

        CountryResponse response = countryMapper.toCountryResponse(testCountry);

        assertNotNull(response);
        assertEquals(testCountry.getCode(), response.code());
        assertNotNull(response.cities());
        assertTrue(response.cities().isEmpty());
    }

    @Test
    void testToCountryResponse_WithEmptyCities_ShouldReturnListWithEmptyString() {
        testCountry.setCities("");

        CountryResponse response = countryMapper.toCountryResponse(testCountry);

        assertNotNull(response);
        assertEquals(testCountry.getCode(), response.code());
        assertNotNull(response.cities());
        assertEquals(1, response.cities().size());
        assertEquals("", response.cities().get(0));
    }

    @Test
    void testToCountryResponse_WithSingleCity_ShouldMapCorrectly() {
        testCountry.setCities("Dakar");

        CountryResponse response = countryMapper.toCountryResponse(testCountry);

        assertNotNull(response);
        assertEquals(testCountry.getCode(), response.code());
        assertNotNull(response.cities());
        assertEquals(1, response.cities().size());
        assertEquals("Dakar", response.cities().get(0));
    }

    @Test
    void testToCountryResponse_WithNullOptionalFields_ShouldMapCorrectly() {
        testCountry.setNameFr(null);
        testCountry.setNameEn(null);
        testCountry.setCities(null);

        CountryResponse response = countryMapper.toCountryResponse(testCountry);

        assertNotNull(response);
        assertEquals(testCountry.getCode(), response.code());
        assertNull(response.nameFr());
        assertNull(response.nameEn());
        assertTrue(response.cities().isEmpty());
    }

    @Test
    void testToCountryResponse_WithNullEntity_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> countryMapper.toCountryResponse(null));
    }

    @Test
    void testToCountryResponse_CitiesWithSpaces_ShouldNotTrim() {
        testCountry.setCities("Dakar, Saint-Louis, Thiès");

        CountryResponse response = countryMapper.toCountryResponse(testCountry);

        assertNotNull(response);
        assertEquals(testCountry.getCode(), response.code());
        assertNotNull(response.cities());
        assertEquals(3, response.cities().size());
        assertEquals("Dakar", response.cities().get(0));
        assertEquals(" Saint-Louis", response.cities().get(1));
        assertEquals(" Thiès", response.cities().get(2));
    }
}
