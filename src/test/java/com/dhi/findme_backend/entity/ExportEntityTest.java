package com.dhi.findme_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExportEntityTest {

    private Export export;
    private User user;
    private Address address;

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

        export = new Export();
        export.setId(UUID.randomUUID());
        export.setCode("EXP-123");
        export.setFilename("ADREES_Maison.pdf");
        export.setDownloadUrl("https://cdn.adrees.africa/exports/EXP-123.pdf");
        export.setExpiresAt(LocalDateTime.now().plusDays(7));
        export.setSize("1.2 MB");
        export.setUser(user);
        export.setAddress(address);
    }

    @Test
    void testExportCreation_WithValidData_ShouldCreateExport() {
        assertNotNull(export.getId());
        assertEquals("EXP-123", export.getCode());
        assertEquals("ADREES_Maison.pdf", export.getFilename());
        assertEquals("https://cdn.adrees.africa/exports/EXP-123.pdf", export.getDownloadUrl());
        assertEquals("1.2 MB", export.getSize());
    }

    @Test
    void testExportCode_ShouldBeSettable() {
        export.setCode("EXP-456");
        assertEquals("EXP-456", export.getCode());
    }

    @Test
    void testExportFilename_ShouldBeSettable() {
        export.setFilename("ADREES_Bureau.pdf");
        assertEquals("ADREES_Bureau.pdf", export.getFilename());
    }

    @Test
    void testExportDownloadUrl_ShouldBeSettable() {
        export.setDownloadUrl("https://cdn.adrees.africa/exports/EXP-456.pdf");
        assertEquals("https://cdn.adrees.africa/exports/EXP-456.pdf", export.getDownloadUrl());
    }

    @Test
    void testExportExpiresAt_ShouldBeSettable() {
        LocalDateTime newExpiry = LocalDateTime.now().plusDays(30);
        export.setExpiresAt(newExpiry);
        assertEquals(newExpiry, export.getExpiresAt());
    }

    @Test
    void testExportSize_ShouldBeSettable() {
        export.setSize("2.5 MB");
        assertEquals("2.5 MB", export.getSize());
    }

    @Test
    void testExportUser_ShouldBeSettable() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail("newuser@example.com");
        
        export.setUser(newUser);
        assertEquals(newUser, export.getUser());
    }

    @Test
    void testExportAddress_ShouldBeSettable() {
        Address newAddress = new Address("Bureau", user);
        newAddress.setId(UUID.randomUUID());
        newAddress.setCode("ADR-5678-XX");
        
        export.setAddress(newAddress);
        assertEquals(newAddress, export.getAddress());
    }
}
