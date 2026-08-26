package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.SupportTicketResponse;
import com.dhi.findme_backend.entity.SupportTicket;
import com.dhi.findme_backend.entity.User;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SupportTicketMapperTest {

    private SupportTicketMapper supportTicketMapper;

    private SupportTicket testTicket;
    private User testUser;

    @BeforeEach
    void setUp() {
        supportTicketMapper = Mappers.getMapper(SupportTicketMapper.class);
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setAvatarUrl("https://example.com/avatar.jpg");

        testTicket = new SupportTicket();
        testTicket.setId(UUID.randomUUID());
        testTicket.setCode("MSG-001");
        testTicket.setSubject("Problème de connexion");
        testTicket.setMessage("Je n'arrive pas à me connecter");
        testTicket.setStatus("Non traité");
        testTicket.setType("technique");
        testTicket.setUser(testUser);
        testTicket.setCreatedAt(LocalDateTime.now());
        testTicket.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testToSupportTicketResponse_WithAllFields_ShouldMapCorrectly() {
        SupportTicketResponse response = supportTicketMapper.toSupportTicketResponse(testTicket);

        assertNotNull(response);
        assertEquals(testTicket.getCode(), response.code());
        assertEquals(testTicket.getSubject(), response.subject());
        assertEquals(testTicket.getMessage(), response.message());
        assertEquals(testTicket.getStatus(), response.status());
        assertEquals(testTicket.getType(), response.type());
        assertEquals(testTicket.getCreatedAt(), response.date());
        assertEquals(testUser.getAvatarUrl(), response.avatarUrl());
    }

    @Test
    void testToSupportTicketResponse_WithNullUser_ShouldNotThrowException() {
        testTicket.setUser(null);

        SupportTicketResponse response = supportTicketMapper.toSupportTicketResponse(testTicket);

        assertNotNull(response);
        assertEquals(testTicket.getCode(), response.code());
        assertNull(response.avatarUrl());
    }

    @Test
    void testToSupportTicketResponse_WithNullCreatedAt_ShouldNotThrowException() {
        testTicket.setCreatedAt(null);

        SupportTicketResponse response = supportTicketMapper.toSupportTicketResponse(testTicket);

        assertNotNull(response);
        assertEquals(testTicket.getCode(), response.code());
        assertNull(response.date());
    }

    @Test
    void testToSupportTicketResponse_WithNullUserAvatarUrl_ShouldNotThrowException() {
        testUser.setAvatarUrl(null);

        SupportTicketResponse response = supportTicketMapper.toSupportTicketResponse(testTicket);

        assertNotNull(response);
        assertEquals(testTicket.getCode(), response.code());
        assertNull(response.avatarUrl());
    }

    @Test
    void testToSupportTicketResponse_WithNullOptionalFields_ShouldMapCorrectly() {
        testTicket.setMessage(null);
        testTicket.setStatus(null);
        testTicket.setType(null);

        SupportTicketResponse response = supportTicketMapper.toSupportTicketResponse(testTicket);

        assertNotNull(response);
        assertEquals(testTicket.getCode(), response.code());
        assertNull(response.message());
        assertNull(response.status());
        assertNull(response.type());
    }

    @Test
    void testToSupportTicketResponse_WithNullEntity_ShouldReturnNull() {
        SupportTicketResponse response = supportTicketMapper.toSupportTicketResponse(null);

        assertNull(response);
    }

    @Test
    void testToSupportTicketResponse_WithEmptyStrings_ShouldMapCorrectly() {
        testTicket.setSubject("");
        testTicket.setMessage("");

        SupportTicketResponse response = supportTicketMapper.toSupportTicketResponse(testTicket);

        assertNotNull(response);
        assertEquals(testTicket.getCode(), response.code());
        assertEquals("", response.subject());
        assertEquals("", response.message());
    }
}
