package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.*;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gestion des utilisateurs")
public class UserController {

    private final UserService userService;
    private final SecurityUtilsInterface securityUtils;

    public UserController(UserService userService, SecurityUtilsInterface securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un utilisateur (Admin)", description = "Crée un nouvel utilisateur avec un rôle et un plan spécifiés")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "422", description = "Email déjà existant ou erreur de validation")
    })
    public ResponseEntity<StandardResponse<UserResponse>> createUserByAdmin(@Valid @RequestBody AdminUserCreateRequest request) {
        UserResponse response = userService.createUserByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(StandardResponse.success(201, "Utilisateur créé avec succès", response, "/api/users"));
    }

    @PutMapping("/{id}/plan")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Changer le plan d'un utilisateur (Admin)", description = "Modifie le plan d'abonnement d'un utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plan mis à jour avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<StandardResponse<UserResponse>> updateUserPlan(@PathVariable UUID id, @Valid @RequestBody UserPlanUpdateRequest request) {
        UserResponse response = userService.updateUserPlan(id, request);
        return ResponseEntity.ok(StandardResponse.success(200, "Plan mis à jour avec succès", response, "/api/users/" + id + "/plan"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un utilisateur (Admin)", description = "Supprime un utilisateur par son ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<StandardResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(StandardResponse.success(200, "Utilisateur supprimé avec succès", "/api/users/" + id));
    }

    @GetMapping("/me")
    @Operation(summary = "Profil utilisateur", description = "Récupère le profil complet de l'utilisateur connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès")
    })
    public ResponseEntity<StandardResponse<UserResponse>> getCurrentUser() {
        UserResponse response = userService.getCurrentUser();
        return ResponseEntity.ok(StandardResponse.success(200, "Profil récupéré avec succès", response, "/api/users/me"));
    }

    @PatchMapping("/me")
    @Operation(summary = "Modifier profil", description = "Met à jour le profil de l'utilisateur connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès")
    })
    public ResponseEntity<StandardResponse<UserResponse>> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        UserResponse response = userService.updateUser(currentUserId, request);
        return ResponseEntity.ok(StandardResponse.success(200, "Profil mis à jour avec succès", response, "/api/users/me"));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Changer mot de passe", description = "Change le mot de passe de l'utilisateur connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès"),
        @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect")
    })
    public ResponseEntity<StandardResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        userService.changePassword(currentUserId, request.oldPassword(), request.newPassword());
        return ResponseEntity.ok(StandardResponse.success(200, "Mot de passe changé avec succès", "/api/users/me/password"));
    }

    @PostMapping("/me/avatar")
    @Operation(summary = "Uploader avatar", description = "Upload une photo de profil pour l'utilisateur connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Avatar uploadé avec succès"),
        @ApiResponse(responseCode = "400", description = "Fichier invalide ou trop volumineux")
    })
    public ResponseEntity<StandardResponse<AvatarUploadResponse>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        AvatarUploadResponse response = userService.uploadAvatar(currentUserId, file);
        return ResponseEntity.ok(StandardResponse.success(200, "Avatar uploadé avec succès", response, "/api/users/me/avatar"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Liste des utilisateurs", description = "Retourne la liste paginée de tous les utilisateurs (Admin uniquement)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<Page<UserResponse>>> getAllUsers(
            @Parameter(description = "Pagination") Pageable pageable,
            @Parameter(description = "Recherche par nom ou email") @RequestParam(required = false) String search,
            @Parameter(description = "Filtre par pays") @RequestParam(required = false) String country,
            @Parameter(description = "Filtre par plan") @RequestParam(required = false) String plan) {
        Page<UserResponse> response = userService.getAllUsers(pageable, search, country, plan);
        return ResponseEntity.ok(StandardResponse.success(200, "Liste des utilisateurs récupérée avec succès", response, "/api/users"));
    }
}