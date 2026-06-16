package com.prode.infrastructure.adapter.inbound.rest;

import com.prode.application.dto.request.PredictionRequest;
import com.prode.application.dto.response.PredictionResponse;
import com.prode.application.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@Tag(name = "Predictions", description = "Prediction management API")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    @Operation(summary = "List predictions", description = "Get predictions with optional filters (matchId, userId)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Successful operation") })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PredictionResponse>> findAll(
            @RequestParam(required = false) Long matchId,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        if (matchId != null) {
            return ResponseEntity.ok(predictionService.findByMatchId(matchId));
        }
        if (userId != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(predictionService.findByUserId(userId));
        }
        // Default: return current user's predictions
        return ResponseEntity.ok(predictionService.findByUserEmail(authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prediction by ID")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PredictionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(predictionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create prediction")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Validation / business rule error") })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PredictionResponse> create(@Valid @RequestBody PredictionRequest request,
                                                      Authentication authentication) {
        PredictionResponse response = predictionService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update prediction")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Updated"),
                    @ApiResponse(responseCode = "400", description = "Validation / business rule error"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PredictionResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody PredictionRequest request,
                                                      Authentication authentication) {
        PredictionResponse response = predictionService.update(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete prediction (sets estado=ELIMINADO)")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Deleted"),
                    @ApiResponse(responseCode = "404", description = "Not found") })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        predictionService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}