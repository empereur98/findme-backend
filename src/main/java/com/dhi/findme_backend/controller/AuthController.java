package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.entity.OtpType;
import com.dhi.findme_backend.service.AuthService;
import com.dhi.findme_backend.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints d'authentification")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final OtpService otpService;

    public AuthController(AuthService authService, OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription", description = "Inscrit un nouvel utilisateur avec email et mot de passe")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
        @ApiResponse(responseCode = "422", description = "Email déjà existant ou erreur de validation")
    })
    public ResponseEntity<StandardResponse<AuthResponse>> register(@Valid @RequestBody AuthRegisterRequest request) {
        AuthResponse response = authService.register(request);
        LOGGER.debug("AuthResponse generated - token: {}, user: {}", response.token(), response.user());
        return ResponseEntity.status(HttpStatus.CREATED).body(StandardResponse.success(201, "Utilisateur créé avec succès", response, "/api/auth/register"));
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Connecte un utilisateur existant via email/mot de passe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<StandardResponse<AuthResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(StandardResponse.success(200, "Connexion réussie", response, "/api/auth/login"));
    }

    @PostMapping("/google")
    @Operation(summary = "Connexion Google", description = "Connecte ou crée un compte via Google OAuth2")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion Google réussie")
    })
    public CompletableFuture<ResponseEntity<StandardResponse<AuthResponse>>> loginWithGoogle(@Valid @RequestBody AuthGoogleRequest request) {
        return authService.loginWithGoogle(request)
                .thenApply(response -> ResponseEntity.ok(StandardResponse.success(200, "Connexion Google réussie", response, "/api/auth/google")));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Mot de passe oublié", description = "Envoie un code OTP par email pour réinitialiser le mot de passe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP envoyé si le compte existe")
    })
    public ResponseEntity<StandardResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        otpService.generateOtp(new OtpRequest(request.email()), OtpType.PASSWORD_RESET);
        return ResponseEntity.ok(StandardResponse.success(200, "Code de réinitialisation envoyé si le compte existe", "/api/auth/forgot-password"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Réinitialisation du mot de passe", description = "Réinitialise le mot de passe avec un code OTP valide")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
        @ApiResponse(responseCode = "400", description = "Code OTP invalide ou expiré")
    })
    public ResponseEntity<StandardResponse<Void>> resetPassword(@Valid @RequestBody OtpResetPasswordRequest request) {
        boolean isValid = otpService.verifyOtp(new OtpVerifyRequest(request.email(), request.otp()), OtpType.PASSWORD_RESET);
        if (isValid) {
            authService.resetPasswordWithOtp(request.email(), request.otp(), request.newPassword());
            return ResponseEntity.ok(StandardResponse.success(200, "Mot de passe réinitialisé avec succès", "/api/auth/reset-password"));
        } else {
            return ResponseEntity.badRequest().body(StandardResponse.success(400, "Code OTP invalide ou expiré", "/api/auth/reset-password"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", description = "Invalide le token de session actuel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    })
    public ResponseEntity<StandardResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(StandardResponse.success(200, "Déconnexion réussie", "/api/auth/logout"));
    }

    @PostMapping("/otp/send-email-verification")
    @Operation(summary = "Envoyer OTP vérification email", description = "Envoie un code OTP par email pour vérifier l'adresse email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP envoyé avec succès")
    })
    public ResponseEntity<StandardResponse<OtpResponse>> sendEmailVerificationOtp(@Valid @RequestBody OtpRequest request) {
        otpService.generateOtp(request, OtpType.EMAIL_VERIFICATION);
        return ResponseEntity.ok(StandardResponse.success(200, "Code de vérification envoyé par email", new OtpResponse("Code de vérification envoyé", request.email()), "/api/auth/otp/send-email-verification"));
    }

    @PostMapping("/otp/verify-email")
    @Operation(summary = "Valider OTP vérification email", description = "Valide le code OTP pour vérifier l'adresse email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email vérifié avec succès"),
        @ApiResponse(responseCode = "400", description = "Code OTP invalide ou expiré")
    })
    public ResponseEntity<StandardResponse<Void>> verifyEmailOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean isValid = otpService.verifyOtp(request, OtpType.EMAIL_VERIFICATION);
        if (isValid) {
            authService.verifyEmail(request.email());
            return ResponseEntity.ok(StandardResponse.success(200, "Email vérifié avec succès", "/api/auth/otp/verify-email"));
        } else {
            return ResponseEntity.badRequest().body(StandardResponse.success(400, "Code OTP invalide ou expiré", "/api/auth/otp/verify-email"));
        }
    }

    @PostMapping("/otp/send-password-reset")
    @Operation(summary = "Envoyer OTP réinitialisation mot de passe", description = "Envoie un code OTP par email pour réinitialiser le mot de passe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP envoyé avec succès")
    })
    public ResponseEntity<StandardResponse<OtpResponse>> sendPasswordResetOtp(@Valid @RequestBody OtpRequest request) {
        otpService.generateOtp(request, OtpType.PASSWORD_RESET);
        return ResponseEntity.ok(StandardResponse.success(200, "Code de réinitialisation envoyé par email", new OtpResponse("Code de réinitialisation envoyé", request.email()), "/api/auth/otp/send-password-reset"));
    }

    @PostMapping("/otp/verify-password-reset")
    @Operation(summary = "Valider OTP réinitialisation mot de passe", description = "Valide le code OTP pour réinitialiser le mot de passe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP validé avec succès"),
        @ApiResponse(responseCode = "400", description = "Code OTP invalide ou expiré")
    })
    public ResponseEntity<StandardResponse<Void>> verifyPasswordResetOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean isValid = otpService.verifyOtp(request, OtpType.PASSWORD_RESET);
        if (isValid) {
            return ResponseEntity.ok(StandardResponse.success(200, "OTP validé avec succès", "/api/auth/otp/verify-password-reset"));
        } else {
            return ResponseEntity.badRequest().body(StandardResponse.success(400, "Code OTP invalide ou expiré", "/api/auth/otp/verify-password-reset"));
        }
    }
}