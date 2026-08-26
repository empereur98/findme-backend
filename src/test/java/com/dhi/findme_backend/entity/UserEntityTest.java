package com.dhi.findme_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("+221771234567");
        user.setAvatarUrl("http://avatar.url");
        user.setDefaultLocation("Dakar");
        user.setVerified(true);
        user.setRole("user");
        user.setAddressesCreatedCount(2);
        user.setMaxAddresses(4);
        user.setPlan("free");
    }

    @Test
    void testUserCreation_WithValidData_ShouldCreateUser() {
        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertTrue(user.getVerified());
        assertEquals("user", user.getRole());
    }

    @Test
    void testUserEmail_ShouldBeSettable() {
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());
    }

    @Test
    void testUserPassword_ShouldBeSettable() {
        user.setPassword("newEncodedPassword");
        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    void testUserRole_ShouldBeSettable() {
        user.setRole("admin");
        assertEquals("admin", user.getRole());
    }

    @Test
    void testUserPlan_ShouldBeSettable() {
        user.setPlan("pro");
        assertEquals("pro", user.getPlan());
    }

    @Test
    void testUserAddressesCount_ShouldBeIncrementable() {
        int initialCount = user.getAddressesCreatedCount();
        user.setAddressesCreatedCount(initialCount + 1);
        assertEquals(initialCount + 1, user.getAddressesCreatedCount());
    }

    @Test
    void testUserMaxAddresses_ShouldBeSettable() {
        user.setMaxAddresses(100);
        assertEquals(100, user.getMaxAddresses());
    }

    @Test
    void testUserVerification_ShouldBeToggleable() {
        user.setVerified(false);
        assertFalse(user.getVerified());
        
        user.setVerified(true);
        assertTrue(user.getVerified());
    }

    @Test
    void testUserPhone_ShouldBeSettable() {
        user.setPhone("+221779876543");
        assertEquals("+221779876543", user.getPhone());
    }

    @Test
    void testUserAvatarUrl_ShouldBeSettable() {
        user.setAvatarUrl("http://new-avatar.url");
        assertEquals("http://new-avatar.url", user.getAvatarUrl());
    }

    @Test
    void testUserDefaultLocation_ShouldBeSettable() {
        user.setDefaultLocation("Saint-Louis");
        assertEquals("Saint-Louis", user.getDefaultLocation());
    }
}
