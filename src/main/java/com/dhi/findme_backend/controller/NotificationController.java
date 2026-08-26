package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.NotificationResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing user notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications utilisateur")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtilsInterface securityUtils;

    /**
     * Constructs a NotificationController with the necessary services.
     *
     * @param notificationService The service for notification-related operations.
     * @param securityUtils       The utility for security-related operations, like getting the current user.
     */
    public NotificationController(NotificationService notificationService, SecurityUtilsInterface securityUtils) {
        this.notificationService = notificationService;
        this.securityUtils = securityUtils;
    }

    /**
     * Retrieves a paginated list of notifications for the currently authenticated user.
     *
     * @param pageable   Pagination information.
     * @param unreadOnly Optional filter to get only unread notifications.
     * @return A {@link ResponseEntity} containing a {@link Page} of {@link NotificationResponse}.
     */
    @GetMapping
    @Operation(summary = "Liste des notifications", description = "Retourne les notifications de l'utilisateur connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<StandardResponse<Page<NotificationResponse>>> getNotifications(
            @Parameter(description = "Pagination") Pageable pageable,
            @Parameter(description = "Filtrer uniquement les non lues") @RequestParam(required = false) Boolean unreadOnly) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        Page<NotificationResponse> response = notificationService.getNotifications(currentUserId, pageable, unreadOnly);
        return ResponseEntity.ok(StandardResponse.success(200, "Liste des notifications récupérée avec succès", response, "/api/notifications"));
    }

    /**
     * Marks a specific notification as read for the currently authenticated user.
     *
     * @param id The UUID of the notification to mark as read.
     * @return A {@link ResponseEntity} containing the updated {@link NotificationResponse}.
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Marquer comme lue", description = "Marque une notification comme lue")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification marquée comme lue"),
        @ApiResponse(responseCode = "404", description = "Notification introuvable")
    })
    public ResponseEntity<StandardResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        NotificationResponse response = notificationService.markAsRead(id, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Notification marquée comme lue", response, "/api/notifications/" + id + "/read"));
    }

    /**
     * Marks all notifications for the currently authenticated user as read.
     *
     * @return A {@link ResponseEntity} with status 200 (OK) and an empty body.
     */
    @PatchMapping("/read-all")
    @Operation(summary = "Marquer toutes comme lues", description = "Marque toutes les notifications de l'utilisateur comme lues")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Toutes les notifications marquées comme lues")
    })
    public ResponseEntity<StandardResponse<Void>> markAllAsRead() {
        UUID currentUserId = securityUtils.getCurrentUserId();
        notificationService.markAllAsRead(currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Toutes les notifications marquées comme lues", "/api/notifications/read-all"));
    }
}
