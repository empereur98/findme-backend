package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.dto.AddressUpdateRequest;
import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.AddressMapper;
import com.dhi.findme_backend.repository.AddressRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.google.openlocationcode.OpenLocationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setAddressesCreatedCount(0);
        user.setMaxAddresses(5);
    }

    @Test
    void createAddress_shouldGenerateRealPlusCode() {
        // Arrange
        double lat = 5.345, lng = -4.024;
        String expectedPlusCode = new OpenLocationCode(lat, lng).getCode();
        AddressCreateRequest request = new AddressCreateRequest("Maison", "CI", "Abidjan", "Cocody", "Rue des jardins", "Près de la pharmacie", lat, lng, "image.jpg", "Personnel");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        addressService.createAddress(request, userId);

        // Assert
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(addressCaptor.capture());
        Address savedAddress = addressCaptor.getValue();

        assertEquals(expectedPlusCode, savedAddress.getCodePlus());
    }
    
    // ... (autres tests)
}