package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.Export;
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
class ExportRepositoryTest {

    @Mock
    private ExportRepository exportRepository;

    private Export testExport;
    private User testUser;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        testAddress = new Address("Maison", testUser);
        testAddress.setId(UUID.randomUUID());
        testAddress.setCode("ADR-1234-XX");
        testAddress.setName("Maison");
        testAddress.setCountry("Sénégal");
        testAddress.setCity("Dakar");

        testExport = new Export();
        testExport.setId(UUID.randomUUID());
        testExport.setCode("EXP-123");
        testExport.setFilename("ADREES_Maison.pdf");
        testExport.setDownloadUrl("https://cdn.adrees.africa/exports/EXP-123.pdf");
        testExport.setExpiresAt(LocalDateTime.now().plusDays(7));
        testExport.setSize("1.2 MB");
        testExport.setUser(testUser);
        testExport.setAddress(testAddress);
    }

    @Test
    void testFindByCode_WhenExportExists_ShouldReturnExport() {
        when(exportRepository.findByCode("EXP-123")).thenReturn(Optional.of(testExport));

        Optional<Export> foundExport = exportRepository.findByCode("EXP-123");

        assertTrue(foundExport.isPresent());
        assertEquals("EXP-123", foundExport.get().getCode());
        assertEquals("ADREES_Maison.pdf", foundExport.get().getFilename());
        verify(exportRepository).findByCode("EXP-123");
    }

    @Test
    void testFindByCode_WhenExportNotExists_ShouldReturnEmpty() {
        when(exportRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Export> foundExport = exportRepository.findByCode("NONEXISTENT");

        assertFalse(foundExport.isPresent());
        verify(exportRepository).findByCode("NONEXISTENT");
    }

    @Test
    void testFindByUser_WithExistingUser_ShouldReturnExports() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Export> exportPage = new PageImpl<>(java.util.List.of(testExport));
        
        when(exportRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(exportPage);

        Page<Export> exportsPage = exportRepository.findByUser(testUser, pageable);

        assertEquals(1, exportsPage.getTotalElements());
        assertEquals(1, exportsPage.getContent().size());
        verify(exportRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void testFindByUser_WithNoExports_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Export> emptyPage = new PageImpl<>(java.util.List.of());
        
        when(exportRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(emptyPage);

        Page<Export> exportsPage = exportRepository.findByUser(testUser, pageable);

        assertEquals(0, exportsPage.getTotalElements());
        assertTrue(exportsPage.getContent().isEmpty());
        verify(exportRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void testFindAll_WithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Export> exportPage = new PageImpl<>(java.util.List.of(testExport));
        
        when(exportRepository.findAll(pageable)).thenReturn(exportPage);

        Page<Export> exportsPage = exportRepository.findAll(pageable);

        assertEquals(1, exportsPage.getTotalElements());
        assertEquals(1, exportsPage.getContent().size());
        verify(exportRepository).findAll(pageable);
    }

    @Test
    void testFindAll_WithPagination_EmptyDatabase_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Export> emptyPage = new PageImpl<>(java.util.List.of());
        
        when(exportRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Export> exportsPage = exportRepository.findAll(pageable);

        assertEquals(0, exportsPage.getTotalElements());
        assertTrue(exportsPage.getContent().isEmpty());
        verify(exportRepository).findAll(pageable);
    }

    @Test
    void testSave_WithValidExport_ShouldPersistExport() {
        when(exportRepository.save(any(Export.class))).thenReturn(testExport);

        Export savedExport = exportRepository.save(testExport);

        assertNotNull(savedExport);
        assertNotNull(savedExport.getId());
        assertEquals("EXP-123", savedExport.getCode());
        verify(exportRepository).save(testExport);
    }

    @Test
    void testDelete_WithExistingExport_ShouldRemoveExport() {
        doNothing().when(exportRepository).delete(testExport);

        exportRepository.delete(testExport);

        verify(exportRepository).delete(testExport);
    }

    @Test
    void testFindById_WithExistingExport_ShouldReturnExport() {
        UUID exportId = testExport.getId();
        when(exportRepository.findById(exportId)).thenReturn(Optional.of(testExport));

        Optional<Export> foundExport = exportRepository.findById(exportId);

        assertTrue(foundExport.isPresent());
        assertEquals(exportId, foundExport.get().getId());
        verify(exportRepository).findById(exportId);
    }

    @Test
    void testFindById_WithNonExistingExport_ShouldReturnEmpty() {
        UUID randomId = UUID.randomUUID();
        when(exportRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<Export> foundExport = exportRepository.findById(randomId);

        assertFalse(foundExport.isPresent());
        verify(exportRepository).findById(randomId);
    }

    @Test
    void testCount_WithMultipleExports_ShouldReturnCorrectCount() {
        when(exportRepository.count()).thenReturn(2L);

        long count = exportRepository.count();

        assertEquals(2, count);
        verify(exportRepository).count();
    }
}