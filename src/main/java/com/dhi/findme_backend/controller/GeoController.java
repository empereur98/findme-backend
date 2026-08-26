package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.CountryResponse;
import com.dhi.findme_backend.dto.StandardResponse;
import com.dhi.findme_backend.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
@Tag(name = "Geo", description = "Référentiels géographiques")
public class GeoController {

    private final CountryService countryService;

    public GeoController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/countries")
    @Operation(summary = "Liste des pays", description = "Retourne la liste des pays supportés avec leurs villes (endpoint public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<StandardResponse<List<CountryResponse>>> getAllCountries() {
        List<CountryResponse> response = countryService.getAllCountries();
        return ResponseEntity.ok(StandardResponse.success(200, "Liste des pays récupérée avec succès", response, "/api/geo/countries"));
    }
}
