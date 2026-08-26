package com.dhi.findme_backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SupportTicketEntityTest {

    private SupportTicket supportTicket;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        supportTicket = new SupportTicket();
        supportTicket.setId(UUID.randomUUID());
        supportTicket.setCode("MSG-001");
        supportTicket.setUserName("Test User");
        supportTicket.setUserEmail("test@example.com");
        supportTicket.setSubject("Problème de connexion");
        supportTicket.setMessage("Je n'arrive pas à me connecter à mon compte");
        supportTicket.setType("technique");
        supportTicket.setStatus("Non traité");
        supportTicket.setCreatedAt(LocalDateTime.now());
        supportTicket.setUser(user);
    }

    @Test
    void testSupportTicketCreation_WithValidData_ShouldCreateSupportTicket() {
        assertNotNull(supportTicket.getId());
        assertEquals("MSG-001", supportTicket.getCode());
        assertEquals("Test User", supportTicket.getUserName());
        assertEquals("test@example.com", supportTicket.getUserEmail());
        assertEquals("Problème de connexion", supportTicket.getSubject());
        assertEquals("Je n'arrive pas à me connecter à mon compte", supportTicket.getMessage());
        assertEquals("technique", supportTicket.getType());
        assertEquals("Non traité", supportTicket.getStatus());
    }

    @Test
    void testSupportTicketCode_ShouldBeSettable() {
        supportTicket.setCode("MSG-002");
        assertEquals("MSG-002", supportTicket.getCode());
    }

    @Test
    void testSupportTicketUserName_ShouldBeSettable() {
        supportTicket.setUserName("New User");
        assertEquals("New User", supportTicket.getUserName());
    }

    @Test
    void testSupportTicketUserEmail_ShouldBeSettable() {
        supportTicket.setUserEmail("newuser@example.com");
        assertEquals("newuser@example.com", supportTicket.getUserEmail());
    }

    @Test
    void testSupportTicketSubject_ShouldBeSettable() {
        supportTicket.setSubject("Facturation incorrecte");
        assertEquals("Facturation incorrecte", supportTicket.getSubject());
    }

    @Test
    void testSupportTicketMessage_ShouldBeSettable() {
        supportTicket.setMessage("Mon abonnement a été facturé deux fois");
        assertEquals("Mon abonnement a été facturé deux fois", supportTicket.getMessage());
    }

    @Test
    void testSupportTicketType_ShouldBeSettable() {
        supportTicket.setType("facturation");
        assertEquals("facturation", supportTicket.getType());
    }

    @Test
    void testSupportTicketStatus_ShouldBeSettable() {
        supportTicket.setStatus("En cours");
        assertEquals("En cours", supportTicket.getStatus());
    }

    @Test
    void testSupportTicketStatus_ShouldBeProgressive() {
        supportTicket.setStatus("En cours");
        assertEquals("En cours", supportTicket.getStatus());
        
        supportTicket.setStatus("Résolu");
        assertEquals("Résolu", supportTicket.getStatus());
        
        supportTicket.setStatus("Fermé");
        assertEquals("Fermé", supportTicket.getStatus());
    }

    @Test
    void testSupportTicketCreatedAt_ShouldBeSettable() {
        LocalDateTime newDate = LocalDateTime.now().plusHours(1);
        supportTicket.setCreatedAt(newDate);
        assertEquals(newDate, supportTicket.getCreatedAt());
    }

    @Test
    void testSupportTicketUser_ShouldBeSettable() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail("newuser@example.com");
        
        supportTicket.setUser(newUser);
        assertEquals(newUser, supportTicket.getUser());
    }

    @Test
    void testSupportTicketType_ShouldSupportMultipleTypes() {
        supportTicket.setType("technique");
        assertEquals("technique", supportTicket.getType());
        
        supportTicket.setType("facturation");
        assertEquals("facturation", supportTicket.getType());
        
        supportTicket.setType("autre");
        assertEquals("autre", supportTicket.getType());
    }
}
