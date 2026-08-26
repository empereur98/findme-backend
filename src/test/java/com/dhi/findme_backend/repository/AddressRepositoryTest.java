package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.Address;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressRepositoryTest {

    @Mock
    private AddressRepository addressRepository;

    private Address testAddress;
    private User testUser;

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
        testAddress.setDistrict("Plateau");
        testAddress.setStreet("Rue de la République");
        testAddress.setLandmark("Près de la grande mosquée");
        testAddress.setGpsLat(14.7167);
        testAddress.setGpsLng(-17.4677);
        testAddress.setImageFacade("http://image.url");
        testAddress.setCodePlus("AB12+34");
        testAddress.setOwnerName("Test User");
        testAddress.setOwnerEmail("test@example.com");
        testAddress.setStatus("En cours");
        testAddress.setAddressDate(LocalDate.now().toString());
        testAddress.setType("Personnel");
        testAddress.setUser(testUser);
    }

    @Test
    void testFindByCode_WhenAddressExists_ShouldReturnAddress() {
        when(addressRepository.findByCode("ADR-1234-XX")).thenReturn(Optional.of(testAddress));

        Optional<Address> foundAddress = addressRepository.findByCode("ADR-1234-XX");

        assertTrue(foundAddress.isPresent());
        assertEquals("ADR-1234-XX", foundAddress.get().getCode());
        assertEquals("Maison", foundAddress.get().getName());
        verify(addressRepository).findByCode("ADR-1234-XX");
    }

    @Test
    void testFindByCode_WhenAddressNotExists_ShouldReturnEmpty() {
        when(addressRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Address> foundAddress = addressRepository.findByCode("NONEXISTENT");

        assertFalse(foundAddress.isPresent());
        verify(addressRepository).findByCode("NONEXISTENT");
    }

    @Test
    void testFindByCodePlus_WhenAddressExists_ShouldReturnAddress() {
        when(addressRepository.findByCodePlus("AB12+34")).thenReturn(Optional.of(testAddress));

        Optional<Address> foundAddress = addressRepository.findByCodePlus("AB12+34");

        assertTrue(foundAddress.isPresent());
        assertEquals("AB12+34", foundAddress.get().getCodePlus());
        assertEquals("Maison", foundAddress.get().getName());
        verify(addressRepository).findByCodePlus("AB12+34");
    }

    @Test
    void testFindByCodePlus_WhenAddressNotExists_ShouldReturnEmpty() {
        when(addressRepository.findByCodePlus("INVALID+99")).thenReturn(Optional.empty());

        Optional<Address> foundAddress = addressRepository.findByCodePlus("INVALID+99");

        assertFalse(foundAddress.isPresent());
        verify(addressRepository).findByCodePlus("INVALID+99");
    }

    @Test
    void testFindByUser_WithExistingUser_ShouldReturnAddresses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(java.util.List.of(testAddress));
        
        when(addressRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(addressPage);

        Page<Address> addressesPage = addressRepository.findByUser(testUser, pageable);

        assertEquals(1, addressesPage.getTotalElements());
        assertEquals(1, addressesPage.getContent().size());
        verify(addressRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void testFindByUser_WithNoAddresses_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> emptyPage = new PageImpl<>(java.util.List.of());
        
        when(addressRepository.findByUser(eq(testUser), any(Pageable.class))).thenReturn(emptyPage);

        Page<Address> addressesPage = addressRepository.findByUser(testUser, pageable);

        assertEquals(0, addressesPage.getTotalElements());
        assertTrue(addressesPage.getContent().isEmpty());
        verify(addressRepository).findByUser(eq(testUser), any(Pageable.class));
    }

    @Test
    void testFindAll_WithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(java.util.List.of(testAddress));
        
        when(addressRepository.findAll(pageable)).thenReturn(addressPage);

        Page<Address> addressesPage = addressRepository.findAll(pageable);

        assertEquals(1, addressesPage.getTotalElements());
        assertEquals(1, addressesPage.getContent().size());
        verify(addressRepository).findAll(pageable);
    }

    @Test
    void testFindAll_WithPagination_EmptyDatabase_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> emptyPage = new PageImpl<>(java.util.List.of());
        
        when(addressRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Address> addressesPage = addressRepository.findAll(pageable);

        assertEquals(0, addressesPage.getTotalElements());
        assertTrue(addressesPage.getContent().isEmpty());
        verify(addressRepository).findAll(pageable);
    }

    @Test
    void testSave_WithValidAddress_ShouldPersistAddress() {
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        Address savedAddress = addressRepository.save(testAddress);

        assertNotNull(savedAddress);
        assertNotNull(savedAddress.getId());
        assertEquals("ADR-1234-XX", savedAddress.getCode());
        verify(addressRepository).save(testAddress);
    }

    @Test
    void testDelete_WithExistingAddress_ShouldRemoveAddress() {
        doNothing().when(addressRepository).delete(testAddress);

        addressRepository.delete(testAddress);

        verify(addressRepository).delete(testAddress);
    }

    @Test
    void testFindById_WithExistingAddress_ShouldReturnAddress() {
        UUID addressId = testAddress.getId();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));

        Optional<Address> foundAddress = addressRepository.findById(addressId);

        assertTrue(foundAddress.isPresent());
        assertEquals(addressId, foundAddress.get().getId());
        verify(addressRepository).findById(addressId);
    }

    @Test
    void testFindById_WithNonExistingAddress_ShouldReturnEmpty() {
        UUID randomId = UUID.randomUUID();
        when(addressRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<Address> foundAddress = addressRepository.findById(randomId);

        assertFalse(foundAddress.isPresent());
        verify(addressRepository).findById(randomId);
    }

    @Test
    void testCount_WithMultipleAddresses_ShouldReturnCorrectCount() {
        when(addressRepository.count()).thenReturn(2L);

        long count = addressRepository.count();

        assertEquals(2, count);
        verify(addressRepository).count();
    }
}