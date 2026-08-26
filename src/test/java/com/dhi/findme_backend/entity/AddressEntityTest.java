package com.dhi.findme_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressEntityTest {

    private Address address;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        address = new Address("Maison", user);
        address.setId(UUID.randomUUID());
        address.setCode("ADR-1234-XX");
        address.setName("Maison");
        address.setCountry("Sénégal");
        address.setCity("Dakar");
        address.setDistrict("Plateau");
        address.setStreet("Rue de la République");
        address.setLandmark("Près de la grande mosquée");
        address.setGpsLat(14.7167);
        address.setGpsLng(-17.4677);
        address.setImageFacade("http://image.url");
        address.setCodePlus("AB12+34");
        address.setOwnerName("Test User");
        address.setOwnerEmail("test@example.com");
        address.setStatus("En cours");
        address.setAddressDate(LocalDate.now().toString());
        address.setType("Personnel");
        address.setUser(user);
    }

    @Test
    void testAddressCreation_WithValidData_ShouldCreateAddress() {
        assertNotNull(address.getId());
        assertEquals("ADR-1234-XX", address.getCode());
        assertEquals("Maison", address.getName());
        assertEquals("Sénégal", address.getCountry());
        assertEquals("Dakar", address.getCity());
    }

    @Test
    void testAddressCode_ShouldBeSettable() {
        address.setCode("ADR-5678-XX");
        assertEquals("ADR-5678-XX", address.getCode());
    }

    @Test
    void testAddressName_ShouldBeSettable() {
        address.setName("Bureau");
        assertEquals("Bureau", address.getName());
    }

    @Test
    void testAddressCountry_ShouldBeSettable() {
        address.setCountry("France");
        assertEquals("France", address.getCountry());
    }

    @Test
    void testAddressCity_ShouldBeSettable() {
        address.setCity("Paris");
        assertEquals("Paris", address.getCity());
    }

    @Test
    void testAddressDistrict_ShouldBeSettable() {
        address.setDistrict("Medina");
        assertEquals("Medina", address.getDistrict());
    }

    @Test
    void testAddressStreet_ShouldBeSettable() {
        address.setStreet("Rue de Medina");
        assertEquals("Rue de Medina", address.getStreet());
    }

    @Test
    void testAddressLandmark_ShouldBeSettable() {
        address.setLandmark("Près du marché");
        assertEquals("Près du marché", address.getLandmark());
    }

    @Test
    void testAddressGpsCoordinates_ShouldBeSettable() {
        address.setGpsLat(14.7167);
        address.setGpsLng(-17.4677);
        assertEquals(14.7167, address.getGpsLat());
        assertEquals(-17.4677, address.getGpsLng());
    }

    @Test
    void testAddressImageFacade_ShouldBeSettable() {
        address.setImageFacade("http://new-image.url");
        assertEquals("http://new-image.url", address.getImageFacade());
    }

    @Test
    void testAddressCodePlus_ShouldBeSettable() {
        address.setCodePlus("CD34+56");
        assertEquals("CD34+56", address.getCodePlus());
    }

    @Test
    void testAddressOwnerName_ShouldBeSettable() {
        address.setOwnerName("New Owner");
        assertEquals("New Owner", address.getOwnerName());
    }

    @Test
    void testAddressOwnerEmail_ShouldBeSettable() {
        address.setOwnerEmail("newowner@example.com");
        assertEquals("newowner@example.com", address.getOwnerEmail());
    }

    @Test
    void testAddressStatus_ShouldBeSettable() {
        address.setStatus("Validé");
        assertEquals("Validé", address.getStatus());
    }

    @Test
    void testAddressType_ShouldBeSettable() {
        address.setType("Professionnel");
        assertEquals("Professionnel", address.getType());
    }

    @Test
    void testAddressUser_ShouldBeSettable() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail("newuser@example.com");
        
        address.setUser(newUser);
        assertEquals(newUser, address.getUser());
    }
}
