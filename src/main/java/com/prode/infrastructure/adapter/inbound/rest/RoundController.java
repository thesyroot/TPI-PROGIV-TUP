package com.prode.infrastructure.adapter.inbound.rest;

import com.prode.application.dto.request.RoundRequest;
import com.prode.application.dto.response.RoundResponse;
import com.prode.application.service.RoundService;
import com.prode.shared.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rounds")
@Tag(name = "Jornadas", description = "Endpoints para gestion de jornadas/fechas")
public class RoundController {

    private final RoundService roundService;

    public RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping
    @Operation(summary = "Listar jornadas", description = "Usar ?page=0&size=20 para paginacion, opcionalmente filtradas por estado")
    @ApiResponses({ @ApiResponse(responseCode = "200") })
    public ResponseEntity<ApiResult<List<RoundResponse>>> getAll(
            @Parameter(description = "Filtrar por estado (PROGRAMADA, EN_JUEGO, FINALIZADA)")
            @RequestParam(required = false) String estado,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        if (pageable.getPageSize() > 0 && pageable.getPageNumber() >= 0) {
            Page<RoundResponse> page = roundService.findAll(estado, pageable);
            return ResponseEntity.ok(ApiResult.ok(page.getContent()));
        }
        return ResponseEntity.ok(ApiResult.ok(roundService.findAll(estado)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener jornada por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Jornada encontrada"),
        @ApiResponse(responseCode = "404", description = "Jornada no encontrada")
    })
    public ResponseEntity<ApiResult<RoundResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResult.ok(roundService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear jornada")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Jornada creada"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o nombre duplicado")
    })
    public ResponseEntity<ApiResult<RoundResponse>> create(@Valid @RequestBody RoundRequest request) {
        RoundResponse round = roundService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.ok("Jornada creada exitosamente", round));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar jornada")
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<ApiResult<RoundResponse>> update(
            @PathVariable Long id, @Valid @RequestBody RoundRequest request) {
        return ResponseEntity.ok(ApiResult.ok("Jornada actualizada exitosamente", roundService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar jornada", description = "Solo si esta en estado PROGRAMADA y sin partidos")
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "Tiene partidos asociados o no esta PROGRAMADA"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) {
        roundService.delete(id);
        return ResponseEntity.ok(ApiResult.ok("Jornada eliminada exitosamente", null));
    }
}
