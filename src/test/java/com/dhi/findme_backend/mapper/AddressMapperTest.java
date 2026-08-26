package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.User;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressMapperTest {

    private AddressMapper addressMapper;

    private Address testAddress;

    @BeforeEach
    void setUp() {
        addressMapper = Mappers.getMapper(AddressMapper.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        testAddress = new Address("Maison", user);
        testAddress.setId(UUID.randomUUID());
        testAddress.setCode("ADR-1234-XX");
        testAddress.setCountry("Sénégal");
        testAddress.setCity("Dakar");
        testAddress.setGpsLat(14.7167);
        testAddress.setGpsLng(-17.4677);
        testAddress.setStatus("En cours");
        testAddress.setCreatedAt(LocalDateTime.now());
        testAddress.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testToAddressResponse_WithAllFields_ShouldMapCorrectly() {
        AddressResponse response = addressMapper.toAddressResponse(testAddress);

        assertNotNull(response);
        assertEquals(testAddress.getCode(), response.code());
        assertEquals(testAddress.getName(), response.name());
        assertEquals(testAddress.getCountry(), response.country());
        assertEquals(testAddress.getCity(), response.city());
        assertEquals(testAddress.getGpsLat(), response.gps().lat());
        assertEquals(testAddress.getGpsLng(), response.gps().lng());
        assertEquals(testAddress.getStatus(), response.status());
    }

    @Test
    void testToAddressResponse_WithNullGpsCoordinates_ShouldNotThrowException() {
        testAddress.setGpsLat(null);
        testAddress.setGpsLng(null);

        AddressResponse response = addressMapper.toAddressResponse(testAddress);

        assertNotNull(response);
        assertEquals(testAddress.getCode(), response.code());
        assertNotNull(response.gps());
        assertNull(response.gps().lat());
        assertNull(response.gps().lng());
    }

    @Test
    void testToAddressResponse_WithNullLatOnly_ShouldNotThrowException() {
        testAddress.setGpsLat(null);
        testAddress.setGpsLng(-17.4677);

        AddressResponse response = addressMapper.toAddressResponse(testAddress);

        assertNotNull(response);
        assertEquals(testAddress.getCode(), response.code());
        assertNotNull(response.gps());
        assertNull(response.gps().lat());
        assertEquals(testAddress.getGpsLng(), response.gps().lng());
    }

    @Test
    void testToAddressResponse_WithNullLngOnly_ShouldNotThrowException() {
        testAddress.setGpsLat(14.7167);
        testAddress.setGpsLng(null);

        AddressResponse response = addressMapper.toAddressResponse(testAddress);

        assertNotNull(response);
        assertEquals(testAddress.getCode(), response.code());
        assertNotNull(response.gps());
        assertEquals(testAddress.getGpsLat(), response.gps().lat());
        assertNull(response.gps().lng());
    }

    @Test
    void testToAddressResponse_WithNullOptionalFields_ShouldMapCorrectly() {
        testAddress.setCity(null);
        testAddress.setStatus(null);

        AddressResponse response = addressMapper.toAddressResponse(testAddress);

        assertNotNull(response);
        assertEquals(testAddress.getCode(), response.code());
        assertNull(response.city());
        assertNull(response.status());
    }

    @Test
    void testToAddressResponse_WithNullEntity_ShouldReturnNull() {
        AddressResponse response = addressMapper.toAddressResponse(null);

        assertNull(response);
    }

    @Test
    void testToAddressResponse_WithZeroCoordinates_ShouldMapCorrectly() {
        testAddress.setGpsLat(0.0);
        testAddress.setGpsLng(0.0);

        AddressResponse response = addressMapper.toAddressResponse(testAddress);

        assertNotNull(response);
        assertEquals(testAddress.getCode(), response.code());
        assertEquals(0.0, response.gps().lat());
        assertEquals(0.0, response.gps().lng());
    }
}
