package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.UserResponse;
import com.dhi.findme_backend.entity.User;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("Password123!");
        testUser.setPhone("+221771234567");
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        testUser.setDefaultLocation("Dakar, Sénégal");
        testUser.setVerified(true);
        testUser.setRole("user");
        testUser.setAddressesCreatedCount(5);
        testUser.setMaxAddresses(10);
        testUser.setPlan("free");
        testUser.setRegistrationDate(LocalDate.now());
    }

    @Test
    void testToUserResponse_WithAllFields_ShouldMapCorrectly() {
        UserResponse response = userMapper.toUserResponse(testUser);

        assertNotNull(response);
        assertEquals(testUser.getFirstName(), response.firstName());
        assertEquals(testUser.getLastName(), response.lastName());
        assertEquals(testUser.getEmail(), response.email());
        assertEquals(testUser.getPhone(), response.phone());
        assertEquals(testUser.getAvatarUrl(), response.avatarUrl());
        assertEquals(testUser.getDefaultLocation(), response.defaultLocation());
        assertEquals(testUser.getVerified(), response.verified());
        assertEquals(testUser.getRole(), response.role());
        assertEquals(testUser.getAddressesCreatedCount(), response.addressesCreatedCount());
        assertEquals(testUser.getMaxAddresses(), response.maxAddresses());
        assertEquals(testUser.getPlan(), response.plan());
        assertEquals(testUser.getRegistrationDate(), response.registrationDate());
    }

    @Test
    void testToUserResponse_WithNullRegistrationDate_ShouldNotThrowException() {
        testUser.setRegistrationDate(null);

        UserResponse response = userMapper.toUserResponse(testUser);

        assertNotNull(response);
        assertEquals(testUser.getFirstName(), response.firstName());
        assertNull(response.registrationDate());
    }

    @Test
    void testToUserResponse_WithNullOptionalFields_ShouldMapCorrectly() {
        testUser.setPhone(null);
        testUser.setAvatarUrl(null);
        testUser.setDefaultLocation(null);

        UserResponse response = userMapper.toUserResponse(testUser);

        assertNotNull(response);
        assertEquals(testUser.getFirstName(), response.firstName());
        assertNull(response.phone());
        assertNull(response.avatarUrl());
        assertNull(response.defaultLocation());
    }

    @Test
    void testToUserResponse_WithNullEntity_ShouldReturnNull() {
        UserResponse response = userMapper.toUserResponse(null);

        assertNull(response);
    }

    @Test
    void testToUserResponse_RegistrationDateMapping_ShouldMapDirectly() {
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        testUser.setRegistrationDate(testDate);

        UserResponse response = userMapper.toUserResponse(testUser);

        assertNotNull(response);
        assertNotNull(response.registrationDate());
        assertEquals(testDate, response.registrationDate());
    }

    @Test
    void testToUserResponse_WithZeroValues_ShouldMapCorrectly() {
        testUser.setAddressesCreatedCount(0);
        testUser.setMaxAddresses(0);
        testUser.setVerified(false);

        UserResponse response = userMapper.toUserResponse(testUser);

        assertNotNull(response);
        assertEquals(0, response.addressesCreatedCount());
        assertEquals(0, response.maxAddresses());
        assertFalse(response.verified());
    }
}
