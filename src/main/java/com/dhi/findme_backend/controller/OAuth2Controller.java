package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.AuthResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/oauth2")
@Tag(name = "OAuth2 Authentication", description = "Endpoints d'authentification OAuth2")
public class OAuth2Controller {

    private final AuthServiceImpl authService;

    public OAuth2Controller(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @GetMapping("/callback/google")
    @Operation(summary = "Callback Google OAuth2", description = "Gère le callback OAuth2 de Google et retourne un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentification réussie"),
        @ApiResponse(responseCode = "400", description = "Erreur lors de l'authentification OAuth2")
    })
    public ResponseEntity<StandardResponse<AuthResponse>> googleCallback(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.badRequest().build();
        }
        
        AuthResponse response = authService.processOAuth2User(oAuth2User, "google");
        return ResponseEntity.ok(StandardResponse.success(200, "Authentification OAuth2 réussie", response, "/api/auth/oauth2/callback/google"));
    }

    @GetMapping("/login")
    @Operation(summary = "Page de login OAuth2", description = "Redirige vers la page de login OAuth2")
    public String loginPage() {
        return "redirect:/oauth2/authorization/google";
    }
}
