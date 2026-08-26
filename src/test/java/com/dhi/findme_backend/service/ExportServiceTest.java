package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.ExportResponse;
import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.Export;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.ExportMapper;
import com.dhi.findme_backend.repository.AddressRepository;
import com.dhi.findme_backend.repository.ExportRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.impl.ExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private ExportRepository exportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ExportMapper exportMapper;
    @InjectMocks
    private ExportServiceImpl exportService;

    @Captor
    private ArgumentCaptor<Export> exportCaptor;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User proUser;
    private User freeUser;
    private Address address;
    private UUID userId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        proUser = new User();
        proUser.setId(userId);
        proUser.setPlan("pro");

        freeUser = new User();
        freeUser.setId(UUID.randomUUID());
        freeUser.setPlan("free");

        address = new Address("Home", proUser);
        address.setId(addressId);
        address.setName("Home");
    }

    @Test
    void generatePdfExport_whenUserIsPro_shouldCreateAndReturnExport() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(exportRepository.save(any(Export.class))).thenAnswer(i -> i.getArgument(0));
        ExportResponse exportResponse = new ExportResponse("EXP-123", "export.pdf", "http://url", LocalDateTime.now(), "1.2 MB");
        when(exportMapper.toExportResponse(any(Export.class))).thenReturn(exportResponse);

        // Act
        exportService.generatePdfExport(addressId, userId);

        // Assert
        verify(exportRepository).save(exportCaptor.capture());
        Export savedExport = exportCaptor.getValue();
        assertEquals(userId, savedExport.getUser().getId());
        assertEquals(addressId, savedExport.getAddress().getId());
        assertNotNull(savedExport.getCode());
    }

    @Test
    void generatePdfExport_whenUserIsFree_shouldThrowBusinessException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(freeUser));

        // Act & Assert
        assertThrows(BusinessException.class, () -> exportService.generatePdfExport(addressId, userId));
        verify(exportRepository, never()).save(any());
    }

    @Test
    void generatePdfExport_whenUserNotFound_shouldThrowNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> exportService.generatePdfExport(addressId, userId));
    }

    @Test
    void generatePdfExport_whenAddressNotFound_shouldThrowNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> exportService.generatePdfExport(addressId, userId));
    }

    @Test
    void getExportHistory_whenUserExists_shouldReturnPageOfExports() {
        // Arrange
        Page<Export> exportPage = new PageImpl<>(Collections.singletonList(new Export()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));
        when(exportRepository.findByUser(proUser, PageRequest.of(0, 10))).thenReturn(exportPage);

        // Act
        Page<ExportResponse> result = exportService.getExportHistory(PageRequest.of(0, 10), userId);

        // Assert
        assertFalse(result.isEmpty());
        verify(exportMapper, times(1)).toExportResponse(any(Export.class));
    }

    @Test
    void cancelSubscription_whenUserExists_shouldSetPlanToFree() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(proUser));

        // Act
        exportService.cancelSubscription(userId);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertEquals("free", updatedUser.getPlan());
    }

    @Test
    void cancelSubscription_whenUserNotFound_shouldThrowNotFoundException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> exportService.cancelSubscription(userId));
        verify(userRepository, never()).save(any());
    }
}