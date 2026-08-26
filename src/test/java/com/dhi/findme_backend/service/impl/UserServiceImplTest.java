package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.UserMapper;
import com.dhi.findme_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @TempDir
    Path tempDir;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setFirstName("Old");
        user.setLastName("Name");
        user.setEmail("test@example.com");
        user.setPassword("encodedOldPassword");
        user.setPlan("free");
        user.setMaxAddresses(4);

        String uploadPath = tempDir.resolve("uploads").toString();
        ReflectionTestUtils.setField(userService, "uploadDir", uploadPath);
        ReflectionTestUtils.setField(userService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateAndReturnUser() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest("New", "Name", "12345", null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(i -> new UserResponse(userId.toString(), "New", "Name", "test@example.com", "12345", null, null, false, "user", 0, 4, "free", null));

        // Act
        userService.updateUser(userId, request);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("New", savedUser.getFirstName());
        assertEquals("Name", savedUser.getLastName());
        assertEquals("12345", savedUser.getPhone());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        UUID nonExistentUserId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("New", "Name", null, null, null);
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(nonExistentUserId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_withValidOldPassword_shouldChangePassword() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        userService.changePassword(userId, "oldPassword", "newPassword");

        // Assert
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedNewPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void changePassword_withInvalidOldPassword_shouldThrowBusinessException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.changePassword(userId, "wrongOldPassword", "newPassword"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserByAdmin_withValidData_shouldCreateAndReturnUser() {
        // Arrange
        AdminUserCreateRequest request = new AdminUserCreateRequest("Admin", "Created", "admin.created@example.com", "password", "admin", "premium");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(i -> new UserResponse(null, "Admin", "Created", "admin.created@example.com", null, null, null, true, "admin", 0, 4, "premium", null));

        // Act
        userService.createUserByAdmin(request);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Admin", savedUser.getFirstName());
        assertEquals("admin.created@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.getVerified());
        assertEquals("admin", savedUser.getRole());
        assertEquals("premium", savedUser.getPlan());
    }

    @Test
    void createUserByAdmin_whenEmailExists_shouldThrowBusinessException() {
        // Arrange
        AdminUserCreateRequest request = new AdminUserCreateRequest("Test", "User", "test@example.com", "password", null, null);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.createUserByAdmin(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_whenUserExists_shouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void updateUserPlan_whenUserExists_shouldUpdatePlanAndLimits() {
        // Arrange
        UserPlanUpdateRequest request = new UserPlanUpdateRequest("premium");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toUserResponse(any(User.class))).thenAnswer(i -> new UserResponse(null, null, null, null, null, null, null, false, null, 0, 100, "premium", null));

        // Act
        userService.updateUserPlan(userId, request);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("premium", savedUser.getPlan());
        assertEquals(100, savedUser.getMaxAddresses());
    }
    
    @Test
    void updateUserPlan_whenUserDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        UserPlanUpdateRequest request = new UserPlanUpdateRequest("premium");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserPlan(userId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void uploadAvatar_withValidFile_shouldSucceed() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "image-content".getBytes());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        AvatarUploadResponse response = userService.uploadAvatar(userId, file);

        // Assert
        assertNotNull(response.avatarUrl());
        assertTrue(response.avatarUrl().startsWith("http://localhost:8080/uploads/avatars/"));
        verify(userRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().getAvatarUrl().contains(userId.toString()));

        String fileName = response.avatarUrl().substring(response.avatarUrl().lastIndexOf("/") + 1);
        Path expectedFile = tempDir.resolve("uploads").resolve("avatars").resolve(fileName);
        assertTrue(Files.exists(expectedFile));
    }
}