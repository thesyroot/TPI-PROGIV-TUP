package com.prode.infrastructure.adapter.inbound.rest;

import com.prode.application.dto.request.TeamRequest;
import com.prode.application.dto.response.TeamResponse;
import com.prode.application.service.TeamService;
import com.prode.shared.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/teams")
@Tag(name = "Equipos", description = "Endpoints para gestion de equipos deportivos")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    @Operation(summary = "Listar equipos activos", description = "Usar ?page=0&size=20 para paginacion")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de equipos obtenida correctamente")
    })
    public ResponseEntity<ApiResult<List<TeamResponse>>> getAll(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        if (pageable.getPageSize() > 0 && pageable.getPageNumber() >= 0) {
            Page<TeamResponse> page = teamService.findAll(pageable);
            return ResponseEntity.ok(ApiResult.ok(page.getContent()));
        }
        List<TeamResponse> teams = teamService.findAll();
        return ResponseEntity.ok(ApiResult.ok(teams));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener equipo por ID", description = "Obtiene un equipo con sus jugadores asociados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo encontrado"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    public ResponseEntity<ApiResult<TeamResponse>> getById(
            @Parameter(description = "ID del equipo") @PathVariable Long id) {
        TeamResponse team = teamService.findById(id);
        return ResponseEntity.ok(ApiResult.ok(team));
    }

    @PostMapping
    @Operation(summary = "Crear equipo", description = "Crea un nuevo equipo deportivo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Equipo creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o nombre duplicado")
    })
    public ResponseEntity<ApiResult<TeamResponse>> create(
            @Valid @RequestBody TeamRequest request) {
        TeamResponse team = teamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok("Equipo creado exitosamente", team));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar equipo", description = "Actualiza los datos de un equipo existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo actualizado"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    public ResponseEntity<ApiResult<TeamResponse>> update(
            @Parameter(description = "ID del equipo") @PathVariable Long id,
            @Valid @RequestBody TeamRequest request) {
        TeamResponse team = teamService.update(id, request);
        return ResponseEntity.ok(ApiResult.ok("Equipo actualizado exitosamente", team));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar equipo (logico)", description = "Desactiva un equipo. No se puede si tiene partidos asociados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo desactivado"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado"),
        @ApiResponse(responseCode = "400", description = "Equipo asociado a partidos")
    })
    public ResponseEntity<ApiResult<Void>> delete(
            @Parameter(description = "ID del equipo") @PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.ok(ApiResult.ok("Equipo eliminado exitosamente", null));
    }
}
