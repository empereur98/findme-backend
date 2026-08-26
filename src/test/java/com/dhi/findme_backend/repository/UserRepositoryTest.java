package com.dhi.findme_backend.repository;

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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhone("+221771234567");
        testUser.setRole("user");
        testUser.setVerified(true);
        testUser.setAddressesCreatedCount(0);
        testUser.setMaxAddresses(4);
        testUser.setPlan("free");
    }

    @Test
    void testFindByEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertEquals("Test", foundUser.get().getFirstName());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindByEmail_WhenUserNotExists_ShouldReturnEmpty() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testFindByPhone_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByPhone("+221771234567")).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userRepository.findByPhone("+221771234567");

        assertTrue(foundUser.isPresent());
        assertEquals("+221771234567", foundUser.get().getPhone());
        assertEquals("test@example.com", foundUser.get().getEmail());
        verify(userRepository).findByPhone("+221771234567");
    }

    @Test
    void testFindByPhone_WhenUserNotExists_ShouldReturnEmpty() {
        when(userRepository.findByPhone("+221999999999")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByPhone("+221999999999");

        assertFalse(foundUser.isPresent());
        verify(userRepository).findByPhone("+221999999999");
    }

    @Test
    void testFindAll_WithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(java.util.List.of(testUser));

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userRepository.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testFindAll_WithPagination_EmptyDatabase_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(java.util.List.of());

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<User> result = userRepository.findAll(pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testSave_WithValidUser_ShouldPersistUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("test@example.com", savedUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void testDelete_WithExistingUser_ShouldRemoveUser() {
        doNothing().when(userRepository).delete(testUser);

        userRepository.delete(testUser);

        verify(userRepository).delete(testUser);
    }

    @Test
    void testFindById_WithExistingUser_ShouldReturnUser() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userRepository.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void testFindById_WithNonExistingUser_ShouldReturnEmpty() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findById(randomId);

        assertFalse(foundUser.isPresent());
        verify(userRepository).findById(randomId);
    }

    @Test
    void testCount_WithMultipleUsers_ShouldReturnCorrectCount() {
        when(userRepository.count()).thenReturn(2L);

        long count = userRepository.count();

        assertEquals(2, count);
        verify(userRepository).count();
    }
}