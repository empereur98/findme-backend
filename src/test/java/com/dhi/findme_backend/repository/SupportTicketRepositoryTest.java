package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.SupportTicket;
import com.dhi.findme_backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketRepositoryTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    private SupportTicket testTicket;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testTicket = new SupportTicket();
        testTicket.setId(UUID.randomUUID());
        testTicket.setCode("MSG-001");
        testTicket.setUserName("Test User");
        testTicket.setUserEmail("test@example.com");
        testTicket.setSubject("Problème de connexion");
        testTicket.setMessage("Je n'arrive pas à me connecter à mon compte");
        testTicket.setType("technique");
        testTicket.setStatus("Non traité");
        testTicket.setCreatedAt(LocalDateTime.now());
        testTicket.setUser(testUser);
    }

    @Test
    void testFindByCode_WhenTicketExists_ShouldReturnTicket() {
        when(supportTicketRepository.findByCode("MSG-001")).thenReturn(Optional.of(testTicket));

        Optional<SupportTicket> foundTicket = supportTicketRepository.findByCode("MSG-001");

        assertTrue(foundTicket.isPresent());
        assertEquals("MSG-001", foundTicket.get().getCode());
        assertEquals("Problème de connexion", foundTicket.get().getSubject());
        verify(supportTicketRepository).findByCode("MSG-001");
    }

    @Test
    void testFindByCode_WhenTicketNotExists_ShouldReturnEmpty() {
        when(supportTicketRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<SupportTicket> foundTicket = supportTicketRepository.findByCode("NONEXISTENT");

        assertFalse(foundTicket.isPresent());
        verify(supportTicketRepository).findByCode("NONEXISTENT");
    }

    @Test
    void testFindByUser_WithExistingUser_ShouldReturnTickets() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SupportTicket> ticketPage = new PageImpl<>(java.util.List.of(testTicket));

        when(supportTicketRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(ticketPage);

        Page<SupportTicket> ticketsPage = supportTicketRepository.findByUser(testUser, pageable);

        assertEquals(1, ticketsPage.getTotalElements());
        assertEquals(1, ticketsPage.getContent().size());
        verify(supportTicketRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void testFindByUser_WithNoTickets_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SupportTicket> emptyPage = new PageImpl<>(java.util.List.of());

        when(supportTicketRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(emptyPage);

        Page<SupportTicket> ticketsPage = supportTicketRepository.findByUser(testUser, pageable);

        assertEquals(0, ticketsPage.getTotalElements());
        assertTrue(ticketsPage.getContent().isEmpty());
        verify(supportTicketRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void testFindAll_WithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SupportTicket> ticketPage = new PageImpl<>(java.util.List.of(testTicket));

        when(supportTicketRepository.findAll(pageable)).thenReturn(ticketPage);

        Page<SupportTicket> ticketsPage = supportTicketRepository.findAll(pageable);

        assertEquals(1, ticketsPage.getTotalElements());
        assertEquals(1, ticketsPage.getContent().size());
        verify(supportTicketRepository).findAll(pageable);
    }

    @Test
    void testFindAll_WithPagination_EmptyDatabase_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SupportTicket> emptyPage = new PageImpl<>(java.util.List.of());

        when(supportTicketRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<SupportTicket> ticketsPage = supportTicketRepository.findAll(pageable);

        assertEquals(0, ticketsPage.getTotalElements());
        assertTrue(ticketsPage.getContent().isEmpty());
        verify(supportTicketRepository).findAll(pageable);
    }

    @Test
    void testSave_WithValidTicket_ShouldPersistTicket() {
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(testTicket);

        SupportTicket savedTicket = supportTicketRepository.save(testTicket);

        assertNotNull(savedTicket);
        assertNotNull(savedTicket.getId());
        assertEquals("MSG-001", savedTicket.getCode());
        verify(supportTicketRepository).save(testTicket);
    }

    @Test
    void testDelete_WithExistingTicket_ShouldRemoveTicket() {
        doNothing().when(supportTicketRepository).delete(testTicket);

        supportTicketRepository.delete(testTicket);

        verify(supportTicketRepository).delete(testTicket);
    }

    @Test
    void testFindById_WithExistingTicket_ShouldReturnTicket() {
        UUID ticketId = testTicket.getId();
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(testTicket));

        Optional<SupportTicket> foundTicket = supportTicketRepository.findById(ticketId);

        assertTrue(foundTicket.isPresent());
        assertEquals(ticketId, foundTicket.get().getId());
        verify(supportTicketRepository).findById(ticketId);
    }

    @Test
    void testFindById_WithNonExistingTicket_ShouldReturnEmpty() {
        UUID randomId = UUID.randomUUID();
        when(supportTicketRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<SupportTicket> foundTicket = supportTicketRepository.findById(randomId);

        assertFalse(foundTicket.isPresent());
        verify(supportTicketRepository).findById(randomId);
    }

    @Test
    void testCount_WithMultipleTickets_ShouldReturnCorrectCount() {
        when(supportTicketRepository.count()).thenReturn(2L);

        long count = supportTicketRepository.count();

        assertEquals(2, count);
        verify(supportTicketRepository).count();
    }
}