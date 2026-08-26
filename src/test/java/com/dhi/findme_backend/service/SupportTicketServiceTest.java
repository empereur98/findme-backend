package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.SupportTicketCreateRequest;
import com.dhi.findme_backend.dto.SupportTicketResponse;
import com.dhi.findme_backend.dto.SupportTicketTriageRequest;
import com.dhi.findme_backend.entity.SupportTicket;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.SupportTicketMapper;
import com.dhi.findme_backend.repository.SupportTicketRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.impl.SupportTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SupportTicketMapper supportTicketMapper;

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    private User testUser;
    private SupportTicket testTicket;
    private SupportTicketResponse ticketResponse;
    private SupportTicketCreateRequest createRequest;
    private UUID userId;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ticketId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");

        createRequest = new SupportTicketCreateRequest(
                "Problème de connexion",
                "Je n'arrive pas à me connecter à mon compte",
                "technique"
        );

        testTicket = new SupportTicket();
        testTicket.setId(ticketId);
        testTicket.setCode("MSG-001");
        testTicket.setUserName("John Doe");
        testTicket.setUserEmail("john.doe@example.com");
        testTicket.setSubject("Problème de connexion");
        testTicket.setMessage("Je n'arrive pas à me connecter à mon compte");
        testTicket.setType("technique");
        testTicket.setStatus("Non traité");
        testTicket.setCreatedAt(LocalDateTime.now());
        testTicket.setUser(testUser);

        ticketResponse = new SupportTicketResponse(
                ticketId.toString(),
                "MSG-001",
                "John Doe",
                "john.doe@example.com",
                "Problème de connexion",
                "Je n'arrive pas à me connecter à mon compte",
                "technique",
                "Non traité",
                LocalDateTime.now(),
                "http://avatar.url"
        );
    }

    @Test
    void testCreateTicket_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(testTicket);
        when(supportTicketMapper.toSupportTicketResponse(any(SupportTicket.class))).thenReturn(ticketResponse);

        var response = supportTicketService.createTicket(createRequest, userId);

        assertNotNull(response);
        assertEquals("Problème de connexion", response.subject());
        assertEquals("technique", response.type());
        assertEquals("Non traité", response.status());
        verify(userRepository).findById(userId);
        verify(supportTicketRepository).save(any(SupportTicket.class));
    }

    @Test
    void testCreateTicket_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportTicketService.createTicket(createRequest, userId));
        verify(supportTicketRepository, never()).save(any(SupportTicket.class));
    }

    @Test
    void testCreateTicket_DifferentTypes() {
        SupportTicketCreateRequest billingRequest = new SupportTicketCreateRequest(
                "Facturation incorrecte",
                "Mon abonnement a été facturé deux fois",
                "facturation"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(testTicket);
        when(supportTicketMapper.toSupportTicketResponse(any(SupportTicket.class))).thenReturn(ticketResponse);

        var response = supportTicketService.createTicket(billingRequest, userId);

        assertNotNull(response);
        verify(supportTicketRepository).save(any(SupportTicket.class));
    }

    @Test
    void testGetTickets_Success() {
        Page<SupportTicket> ticketPage = new PageImpl<>(java.util.List.of(testTicket));
        when(supportTicketRepository.findAll(any(Pageable.class))).thenReturn(ticketPage);
        when(supportTicketMapper.toSupportTicketResponse(any(SupportTicket.class))).thenReturn(ticketResponse);

        Pageable pageable = PageRequest.of(0, 10);
        var response = supportTicketService.getTickets(pageable, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(supportTicketRepository).findAll(pageable);
    }

    @Test
    void testGetTickets_EmptyList() {
        Page<SupportTicket> emptyPage = new PageImpl<>(java.util.List.of());
        when(supportTicketRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Pageable pageable = PageRequest.of(0, 10);
        var response = supportTicketService.getTickets(pageable, null, null, null);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(supportTicketRepository).findAll(pageable);
    }

    @Test
    void testGetTickets_WithFilters() {
        Page<SupportTicket> ticketPage = new PageImpl<>(java.util.List.of(testTicket));
        when(supportTicketRepository.findAll(any(Pageable.class))).thenReturn(ticketPage);
        when(supportTicketMapper.toSupportTicketResponse(any(SupportTicket.class))).thenReturn(ticketResponse);

        Pageable pageable = PageRequest.of(0, 10);
        var response = supportTicketService.getTickets(pageable, "connexion", "Non traité", "technique");

        assertNotNull(response);
        verify(supportTicketRepository).findAll(pageable);
    }

    @Test
    void testTriageTicket_Success() {
        SupportTicketTriageRequest triageRequest = new SupportTicketTriageRequest("En cours");
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(testTicket));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(testTicket);
        when(supportTicketMapper.toSupportTicketResponse(any(SupportTicket.class))).thenReturn(ticketResponse);

        var response = supportTicketService.triageTicket(ticketId, triageRequest);

        assertNotNull(response);
        verify(supportTicketRepository).findById(ticketId);
        verify(supportTicketRepository).save(testTicket);
    }

    @Test
    void testTriageTicket_NotFound() {
        SupportTicketTriageRequest triageRequest = new SupportTicketTriageRequest("En cours");
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> supportTicketService.triageTicket(ticketId, triageRequest));
        verify(supportTicketRepository, never()).save(any(SupportTicket.class));
    }

}
