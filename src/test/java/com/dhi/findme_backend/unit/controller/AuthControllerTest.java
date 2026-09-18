package com.dhi.findme_backend.unit.controller;

import com.dhi.findme_backend.controller.*;
import com.dhi.findme_backend.service.*;
import com.dhi.findme_backend.entity.*;
import com.dhi.findme_backend.exception.*;
import com.dhi.findme_backend.security.*;
import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.entity.OtpType;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.GlobalExceptionHandler;
import com.dhi.findme_backend.service.AuthService;
import com.dhi.findme_backend.service.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserResponse testUserResponse;
    private AuthResponse testAuthResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testUserResponse = new UserResponse(
                "user-id-123", "Jean", "Dupont", "jean.dupont@example.com",
                null, "+237697000000", null, true, "USER", 0, 5, "FREE", null
        );
        testAuthResponse = new AuthResponse("mocked-jwt-token", testUserResponse);
    }

    @Test
    void register_withValidRequest_shouldReturnCreated() throws Exception {
        AuthRegisterRequest request = new AuthRegisterRequest(
                "Jean", "Dupont", "jean.dupont@example.com", "Password123!"
        );
        when(authService.register(any(AuthRegisterRequest.class))).thenReturn(testAuthResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.data.token", is("mocked-jwt-token")))
                .andExpect(jsonPath("$.data.user.email", is("jean.dupont@example.com")));

        verify(authService).register(any(AuthRegisterRequest.class));
    }

    @Test
    void login_withValidCredentials_shouldReturnOk() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("jean.dupont@example.com", "Password123!");
        when(authService.login(any(AuthLoginRequest.class))).thenReturn(testAuthResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.token", is("mocked-jwt-token")));

        verify(authService).login(any(AuthLoginRequest.class));
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("jean.dupont@example.com", "WrongPassword");
        when(authService.login(any(AuthLoginRequest.class))).thenThrow(new BusinessException("INVALID_CREDENTIALS", "Identifiants invalides"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithGoogle_withValidToken_shouldReturnOk() throws Exception {
        AuthGoogleRequest request = new AuthGoogleRequest("google-id-token");
        when(authService.loginWithGoogle(any(AuthGoogleRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(testAuthResponse));

        var mvcResult = mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.token", is("mocked-jwt-token")));
    }

    @Test
    void forgotPassword_withValidEmail_shouldReturnOk() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest("jean.dupont@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        verify(otpService).generateOtp(any(OtpRequest.class), eq(OtpType.PASSWORD_RESET));
    }

    @Test
    void resetPassword_withValidOtp_shouldReturnOk() throws Exception {
        OtpResetPasswordRequest request = new OtpResetPasswordRequest(
                "jean.dupont@example.com", "123456", "NewPassword123!"
        );
        when(otpService.verifyOtp(any(OtpVerifyRequest.class), eq(OtpType.PASSWORD_RESET))).thenReturn(true);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        verify(authService).resetPasswordWithOtp("jean.dupont@example.com", "123456", "NewPassword123!");
    }

    @Test
    void resetPassword_withInvalidOtp_shouldReturnBadRequest() throws Exception {
        OtpResetPasswordRequest request = new OtpResetPasswordRequest(
                "jean.dupont@example.com", "000000", "NewPassword123!"
        );
        when(otpService.verifyOtp(any(OtpVerifyRequest.class), eq(OtpType.PASSWORD_RESET))).thenReturn(false);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).resetPasswordWithOtp(anyString(), anyString(), anyString());
    }

    @Test
    void logout_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        verify(authService).logout();
    }

    @Test
    void sendEmailVerificationOtp_shouldReturnOk() throws Exception {
        OtpRequest request = new OtpRequest("jean.dupont@example.com");

        mockMvc.perform(post("/api/auth/otp/send-email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        verify(otpService).generateOtp(any(OtpRequest.class), eq(OtpType.EMAIL_VERIFICATION));
    }

    @Test
    void verifyEmailOtp_withValidCode_shouldReturnOk() throws Exception {
        OtpVerifyRequest request = new OtpVerifyRequest("jean.dupont@example.com", "123456");
        when(otpService.verifyOtp(any(OtpVerifyRequest.class), eq(OtpType.EMAIL_VERIFICATION))).thenReturn(true);

        mockMvc.perform(post("/api/auth/otp/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        verify(authService).verifyEmail("jean.dupont@example.com");
    }

    @Test
    void verifyEmailOtp_withInvalidCode_shouldReturnBadRequest() throws Exception {
        OtpVerifyRequest request = new OtpVerifyRequest("jean.dupont@example.com", "000000");
        when(otpService.verifyOtp(any(OtpVerifyRequest.class), eq(OtpType.EMAIL_VERIFICATION))).thenReturn(false);

        mockMvc.perform(post("/api/auth/otp/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).verifyEmail(anyString());
    }

    @Test
    void sendPasswordResetOtp_shouldReturnOk() throws Exception {
        OtpRequest request = new OtpRequest("jean.dupont@example.com");

        mockMvc.perform(post("/api/auth/otp/send-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));

        verify(otpService).generateOtp(any(OtpRequest.class), eq(OtpType.PASSWORD_RESET));
    }

    @Test
    void verifyPasswordResetOtp_withValidCode_shouldReturnOk() throws Exception {
        OtpVerifyRequest request = new OtpVerifyRequest("jean.dupont@example.com", "123456");
        when(otpService.verifyOtp(any(OtpVerifyRequest.class), eq(OtpType.PASSWORD_RESET))).thenReturn(true);

        mockMvc.perform(post("/api/auth/otp/verify-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)));
    }

    @Test
    void verifyPasswordResetOtp_withInvalidCode_shouldReturnBadRequest() throws Exception {
        OtpVerifyRequest request = new OtpVerifyRequest("jean.dupont@example.com", "999999");
        when(otpService.verifyOtp(any(OtpVerifyRequest.class), eq(OtpType.PASSWORD_RESET))).thenReturn(false);

        mockMvc.perform(post("/api/auth/otp/verify-password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

