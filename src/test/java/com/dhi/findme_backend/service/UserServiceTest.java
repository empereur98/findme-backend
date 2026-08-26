package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.UserUpdateRequest;
import com.dhi.findme_backend.dto.UserResponse;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.UserMapper;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private com.dhi.findme_backend.security.SecurityUtilsInterface securityUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("john.doe@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPhone("+221771234567");
        testUser.setAvatarUrl("http://avatar.url");
        testUser.setDefaultLocation("Dakar");

        when(securityUtils.getCurrentUserId()).thenReturn(testUser.getId());

        userResponse = new UserResponse(
                "USR-1234-AD",
                "John",
                "Doe",
                "john.doe@example.com",
                "+221771234567",
                "http://avatar.url",
                "Dakar",
                false,
                "user",
                0,
                4,
                "free",
                java.time.LocalDate.now()
        );

        updateRequest = new UserUpdateRequest(
                "Jane",
                "Smith",
                "+221779876543",
                "Saint-Louis",
                "http://new-avatar.url"
        );
    }

    @Test
    void testGetCurrentUser_Success() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        var response = userService.getCurrentUser();

        assertNotNull(response);
        assertEquals("john.doe@example.com", response.email());
        verify(userRepository).findById(any(UUID.class));
    }

    @Test
    void testGetCurrentUser_NotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
        verify(userRepository).findById(any(UUID.class));
    }

    @Test
    void testUpdateUser_Success() {
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        var response = userService.updateUser(userId, updateRequest);

        assertNotNull(response);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, updateRequest));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(java.util.List.of(testUser));

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        Page<UserResponse> response = userService.getAllUsers(pageable, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllUsers_WithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(java.util.List.of(testUser));

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        Page<UserResponse> response = userService.getAllUsers(pageable, "John", null, null);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
