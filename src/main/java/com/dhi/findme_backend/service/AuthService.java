package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.*;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    AuthResponse register(AuthRegisterRequest request);

    AuthResponse login(AuthLoginRequest request);

    CompletableFuture<AuthResponse> loginWithGoogle(AuthGoogleRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(String token, String newPassword);

    void logout();

    void verifyEmail(String email);

    void resetPasswordWithOtp(String email, String otp, String newPassword);
}