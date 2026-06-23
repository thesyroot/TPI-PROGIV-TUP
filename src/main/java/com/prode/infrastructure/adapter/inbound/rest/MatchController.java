package com.prode.infrastructure.adapter.inbound.rest;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prode.application.dto.request.MatchRequest;
import com.prode.application.dto.response.MatchResponse;
import com.prode.application.service.MatchService;
import com.prode.shared.dto.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Partidos", description = "Endpoints para gestion de partidos")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    @Operation(summary = "Listar partidos", description = "Usar ?page=0&size=20 para paginacion, opcionalmente filtrados por jornada")
    @ApiResponses({ @ApiResponse(responseCode = "200") })
    public ResponseEntity<ApiResult<List<MatchResponse>>> getAll(
            @Parameter(description = "ID de la jornada para filtrar")
            @RequestParam(required = false) Long jornadaId,
            @PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.ASC) Pageable pageable) {
        if (pageable.getPageSize() > 0 && pageable.getPageNumber() >= 0) {
            Page<MatchResponse> page = matchService.findAll(jornadaId, pageable);
            return ResponseEntity.ok(ApiResult.ok(page.getContent()));
        }
        return ResponseEntity.ok(ApiResult.ok(matchService.findAll(jornadaId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener partido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Partido encontrado"),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    public ResponseEntity<ApiResult<MatchResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResult.ok(matchService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear partido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Partido creado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o equipos repetidos")
    })
    public ResponseEntity<ApiResult<MatchResponse>> create(@Valid @RequestBody MatchRequest request) {
        MatchResponse match = matchService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.ok("Partido creado exitosamente", match));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar partido", description = "Solo si el partido esta en estado POR JUGARSE")
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "No se puede modificar un partido en juego o finalizado"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<ApiResult<MatchResponse>> update(
            @PathVariable Long id, @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(ApiResult.ok("Partido actualizado exitosamente", matchService.update(id, request)));
    }

    @PutMapping("/{id}/start")
    @Operation(summary = "Iniciar partido de forma manual", description = "Cambia el estado del partido a EN_JUEGO y actualiza la jornada contenedora")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Partido iniciado correctamente"),
        @ApiResponse(responseCode = "400", description = "El partido no esta en estado POR_JUGARSE"),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    public ResponseEntity<ApiResult<MatchResponse>> startMatch(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResult.ok("Partido iniciado exitosamente", matchService.startMatch(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar partido", description = "Solo si esta POR JUGARSE y sin pronosticos")
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400", description = "Tiene pronosticos o no esta POR JUGARSE"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.ok(ApiResult.ok("Partido eliminado exitosamente", null));
    }
}