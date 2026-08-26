package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.PasswordResetToken;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.UserMapper;
import com.dhi.findme_backend.repository.PasswordResetTokenRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.security.IJwtTokenProvider;
import com.dhi.findme_backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private IJwtTokenProvider tokenProvider;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    void register_withNewEmail_shouldCreateUserAndReturnToken() {
        // Arrange
        AuthRegisterRequest request = new AuthRegisterRequest("John", "Doe", "new@example.com", "password");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenProvider.generateToken(any())).thenReturn("jwt-token");
        UserResponse userResponse = new UserResponse(userId.toString(), "John", "Doe", "new@example.com", null, null, null, true, "user", 0, 4, "free", null);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        // Act
        authService.register(request);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertFalse(savedUser.getVerified());
        assertEquals("user", savedUser.getRole());
    }

    @Test
    void register_withExistingEmail_shouldThrowBusinessException() {
        // Arrange
        AuthRegisterRequest request = new AuthRegisterRequest("John", "Doe", "test@example.com", "password");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BusinessException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        // Arrange
        AuthLoginRequest request = new AuthLoginRequest("test@example.com", "password");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(any())).thenReturn("jwt-token");
        UserResponse userResponse = new UserResponse(userId.toString(), "John", "Doe", "test@example.com", null, null, null, true, "user", 0, 4, "free", null);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_withInvalidEmail_shouldThrowResourceNotFoundException() {
        // Arrange
        AuthLoginRequest request = new AuthLoginRequest("wrong@example.com", "password");
        // Simulate AuthenticationManager throwing an exception due to bad credentials
        when(authenticationManager.authenticate(any())).thenThrow(new ResourceNotFoundException("USER_NOT_FOUND", ""));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void forgotPassword_whenUserExists_shouldCreateAndSaveResetToken() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("test@example.com");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        // Act
        authService.forgotPassword(request);

        // Assert
        verify(tokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getToken());
        assertEquals(user.getId(), savedToken.getUser().getId());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void forgotPassword_whenUserDoesNotExist_shouldNotThrowAndDoNothing() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("wrong@example.com");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> authService.forgotPassword(request));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void resetPassword_withValidToken_shouldResetPasswordAndDeleteToken() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("valid-token", user, LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        authService.resetPassword("valid-token", "newPassword");

        // Assert
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedNewPassword", userCaptor.getValue().getPassword());
        verify(tokenRepository).delete(token);
    }

    @Test
    void resetPassword_withInvalidToken_shouldThrowNotFoundException() {
        // Arrange
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.resetPassword("invalid-token", "newPassword"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_withExpiredToken_shouldThrowBusinessExceptionAndDeleteToken() {
        // Arrange
        PasswordResetToken token = new PasswordResetToken("expired-token", user, LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        // Act & Assert
        assertThrows(BusinessException.class, () -> authService.resetPassword("expired-token", "newPassword"));
        verify(tokenRepository).delete(token);
        verify(userRepository, never()).save(any());
    }
}