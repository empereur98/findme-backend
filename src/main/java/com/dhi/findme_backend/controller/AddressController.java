package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.dto.AddressUpdateRequest;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Addresses", description = "Gestion des adresses")
public class AddressController {

    private final AddressService addressService;
    private final SecurityUtilsInterface securityUtils;

    public AddressController(AddressService addressService, SecurityUtilsInterface securityUtils) {
        this.addressService = addressService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    @Operation(summary = "Liste des adresses", description = "Retourne les adresses de l'utilisateur connecté (ou toutes pour admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<StandardResponse<Page<AddressResponse>>> getAddresses(
            @Parameter(description = "Pagination") Pageable pageable,
            @Parameter(description = "Recherche") @RequestParam(required = false) String search,
            @Parameter(description = "Code pays") @RequestParam(required = false) String country,
            @Parameter(description = "Ville") @RequestParam(required = false) String city,
            @Parameter(description = "Type") @RequestParam(required = false) String type,
            @Parameter(description = "Statut") @RequestParam(required = false) String status) {
        // Filtrer les paramètres de tri pour n'autoriser que les propriétés valides
        Pageable safePageable = validateSort(pageable);
        // Si admin, passer null pour voir toutes les adresses, sinon filtrer par utilisateur
        UUID currentUserId = securityUtils.isAdmin() ? null : securityUtils.getCurrentUserId();
        Page<AddressResponse> response = addressService.getAddresses(safePageable, search, country, city, type, status, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Liste des adresses récupérée avec succès", response, "/api/addresses"));
    }

    private Pageable validateSort(Pageable pageable) {
        java.util.Set<String> validProperties = java.util.Set.of(
            "id", "code", "name", "country", "city", "district", "street",
            "landmark", "gpsLat", "gpsLng", "imageFacade", "codePlus",
            "ownerName", "ownerEmail", "status", "addressDate", "type",
            "createdAt", "updatedAt"
        );

        // Valider et corriger les paramètres de pagination
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        // Si les valeurs sont invalides (trop grandes ou négatives), utiliser des valeurs par défaut
        if (pageSize <= 0 || pageSize > 100 || pageNumber < 0 || pageNumber > 1000000) {
            pageSize = 20;
            pageNumber = 0;
        }

        if (pageable.getSort() == null || pageable.getSort().isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(pageNumber, pageSize);
        }

        java.util.List<org.springframework.data.domain.Sort.Order> validOrders = new java.util.ArrayList<>();
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            if (validProperties.contains(property)) {
                validOrders.add(order);
            }
        }

        if (validOrders.isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(pageNumber, pageSize);
        }

        return org.springframework.data.domain.PageRequest.of(
            pageNumber,
            pageSize,
            org.springframework.data.domain.Sort.by(validOrders)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail adresse", description = "Retourne le détail d'une adresse spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Adresse récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Adresse introuvable"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<AddressResponse>> getAddressById(@PathVariable UUID id) {
        AddressResponse response = addressService.getAddressById(id);
        return ResponseEntity.ok(StandardResponse.success(200, "Adresse récupérée avec succès", response, "/api/addresses/" + id));
    }

    @PostMapping
    @Operation(summary = "Créer adresse", description = "Crée une nouvelle adresse")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Adresse créée avec succès"),
        @ApiResponse(responseCode = "403", description = "Limite du plan atteinte"),
        @ApiResponse(responseCode = "422", description = "Erreur de validation")
    })
    public ResponseEntity<StandardResponse<AddressResponse>> createAddress(@Valid @RequestBody AddressCreateRequest request) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        AddressResponse response = addressService.createAddress(request, currentUserId);
        return ResponseEntity.status(201).body(StandardResponse.success(201, "Adresse créée avec succès", response, "/api/addresses"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier adresse", description = "Met à jour une adresse existante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Adresse mise à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Adresse introuvable"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<AddressResponse>> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressUpdateRequest request) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        AddressResponse response = addressService.updateAddress(id, request, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Adresse mise à jour avec succès", response, "/api/addresses/" + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer adresse", description = "Supprime définitivement une adresse")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Adresse supprimée avec succès"),
        @ApiResponse(responseCode = "404", description = "Adresse introuvable"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<Void>> deleteAddress(@PathVariable UUID id) {
        UUID currentUserId = securityUtils.getCurrentUserId();
        addressService.deleteAddress(id, currentUserId);
        return ResponseEntity.ok(StandardResponse.success(200, "Adresse supprimée avec succès", "/api/addresses/" + id));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Vérifier adresse", description = "Passe le statut d'une adresse à Vérifié (Admin uniquement)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StandardResponse<AddressResponse>> verifyAddress(@PathVariable UUID id) {
        AddressResponse response = addressService.verifyAddress(id);
        return ResponseEntity.ok(StandardResponse.success(200, "Adresse vérifiée avec succès", response, "/api/addresses/" + id + "/verify"));
    }

    @GetMapping("/lookup/{codePlus}")
    @Operation(summary = "Lookup par Code Plus", description = "Recherche une adresse par son Code Plus (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Adresse trouvée"),
        @ApiResponse(responseCode = "404", description = "Adresse introuvable")
    })
    public ResponseEntity<StandardResponse<AddressResponse>> lookupByCodePlus(@PathVariable String codePlus) {
        AddressResponse response = addressService.lookupByCodePlus(codePlus);
        return ResponseEntity.ok(StandardResponse.success(200, "Adresse trouvée", response, "/api/addresses/lookup/" + codePlus));
    }
}