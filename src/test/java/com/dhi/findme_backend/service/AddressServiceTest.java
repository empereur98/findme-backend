package com.dhi.findme_backend.service;

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
import com.dhi.findme_backend.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private AddressServiceImpl addressService;

    @Captor
    private ArgumentCaptor<Address> addressCaptor;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User user;
    private Address address;
    private UUID userId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setAddressesCreatedCount(0);
        user.setMaxAddresses(4);

        address = new Address("Home", user);
        address.setId(addressId);
    }

    @Test
    void createAddress_whenLimitNotReached_shouldCreateAddressAndIncrementCount() {
        // Arrange
        AddressCreateRequest request = new AddressCreateRequest("Work", "Sénégal", "Dakar", "Medina", null, "Mosquée", 14.7167, -17.4677, null, "home");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        AddressResponse response = new AddressResponse(addressId.toString(), null, "Work", null, null, null, null, null, null, null, null, null, null, null, null, null);
        when(addressMapper.toAddressResponse(any(Address.class))).thenReturn(response);

        // Act
        addressService.createAddress(request, userId);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        assertEquals(1, userCaptor.getValue().getAddressesCreatedCount());

        verify(addressRepository).save(addressCaptor.capture());
        assertEquals("Work", addressCaptor.getValue().getName());
    }

    @Test
    void createAddress_whenLimitReached_shouldThrowBusinessException() {
        // Arrange
        user.setAddressesCreatedCount(4);
        AddressCreateRequest request = new AddressCreateRequest("Work", null, null, null, null, null, null, null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BusinessException.class, () -> addressService.createAddress(request, userId));
        verify(addressRepository, never()).save(any());
    }

    @Test
    void getAddresses_whenUserExists_shouldReturnPageOfAddresses() {
        // Arrange
        AddressResponse mockResponse = new AddressResponse(addressId.toString(), "Home", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        Page<Address> addressPage = new PageImpl<>(Collections.singletonList(address));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(addressPage);
        when(addressMapper.toAddressResponse(address)).thenReturn(mockResponse);

        // Act
        Page<AddressResponse> result = addressService.getAddresses(PageRequest.of(0, 10), null, null, null, null, null, userId);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(mockResponse, result.getContent().get(0));
        verify(addressMapper, times(1)).toAddressResponse(address);
    }

    @Test
    void updateAddress_whenAddressExists_shouldUpdateFields() {
        // Arrange
        AddressUpdateRequest request = new AddressUpdateRequest("New Name", "New District", null, null, null, null, null, null);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));
        AddressResponse response = new AddressResponse(addressId.toString(), null, "New Name", null, null, "New District", null, null, null, null, null, null, null, null, null, null);
        when(addressMapper.toAddressResponse(any(Address.class))).thenReturn(response);

        // Act
        addressService.updateAddress(addressId, request, userId);

        // Assert
        verify(addressRepository).save(addressCaptor.capture());
        Address savedAddress = addressCaptor.getValue();
        assertEquals("New Name", savedAddress.getName());
        assertEquals("New District", savedAddress.getDistrict());
    }

    @Test
    void deleteAddress_whenAddressExists_shouldDeleteAddressAndDecrementCount() {
        // Arrange
        user.setAddressesCreatedCount(3);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(address);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        addressService.deleteAddress(addressId, userId);

        // Assert
        verify(addressRepository).delete(address);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(2, userCaptor.getValue().getAddressesCreatedCount());
    }

    @Test
    void verifyAddress_whenAddressExists_shouldUpdateStatus() {
        // Arrange
        address.setStatus("En cours");
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));
        AddressResponse response = new AddressResponse(addressId.toString(), null, null, null, null, null, null, null, null, null, null, null, "Vérifié", null, null, null);
        when(addressMapper.toAddressResponse(any(Address.class))).thenReturn(response);

        // Act
        addressService.verifyAddress(addressId);

        // Assert
        verify(addressRepository).save(addressCaptor.capture());
        assertEquals("Vérifié", addressCaptor.getValue().getStatus());
    }

    @Test
    void lookupByCodePlus_whenCodeExists_shouldReturnAddress() {
        // Arrange
        String codePlus = "ABC+123";
        when(addressRepository.findByCodePlus(codePlus)).thenReturn(Optional.of(address));
        AddressResponse response = new AddressResponse(addressId.toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        when(addressMapper.toAddressResponse(address)).thenReturn(response);

        // Act
        addressService.lookupByCodePlus(codePlus);

        // Assert
        verify(addressRepository).findByCodePlus(codePlus);
        verify(addressMapper).toAddressResponse(address);
    }

    @Test
    void lookupByCodePlus_whenCodeDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        String codePlus = "INVALID";
        when(addressRepository.findByCodePlus(codePlus)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> addressService.lookupByCodePlus(codePlus));
    }
}