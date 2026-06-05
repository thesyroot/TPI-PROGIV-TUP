package com.prode.infrastructure.adapter.inbound.rest;

import com.prode.application.dto.request.PlayerRequest;
import com.prode.application.dto.response.PlayerResponse;
import com.prode.application.service.PlayerService;
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
@RequestMapping("/api")
@Tag(name = "Jugadores", description = "Endpoints para gestion de jugadores")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @Operation(summary = "Listar jugadores activos", description = "Usar ?page=0&size=20 para paginacion")
    @ApiResponses({ @ApiResponse(responseCode = "200") })
    public ResponseEntity<ApiResult<List<PlayerResponse>>> getAll(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        if (pageable.getPageSize() > 0 && pageable.getPageNumber() >= 0) {
            Page<PlayerResponse> page = playerService.findAll(pageable);
            return ResponseEntity.ok(ApiResult.ok(page.getContent()));
        }
        return ResponseEntity.ok(ApiResult.ok(playerService.findAll()));
    }

    @GetMapping("/players/{id}")
    @Operation(summary = "Obtener jugador por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Jugador encontrado"),
        @ApiResponse(responseCode = "404", description = "Jugador no encontrado")
    })
    public ResponseEntity<ApiResult<PlayerResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResult.ok(playerService.findById(id)));
    }

    @GetMapping("/teams/{teamId}/players")
    @Operation(summary = "Listar jugadores por equipo")
    @ApiResponses({ @ApiResponse(responseCode = "200") })
    public ResponseEntity<ApiResult<List<PlayerResponse>>> getByTeam(
            @Parameter(description = "ID del equipo") @PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResult.ok(playerService.findByTeamId(teamId)));
    }

    @PostMapping("/players")
    @Operation(summary = "Crear jugador", description = "Crea un nuevo jugador, opcionalmente asociado a un equipo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Jugador creado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    public ResponseEntity<ApiResult<PlayerResponse>> create(@Valid @RequestBody PlayerRequest request) {
        PlayerResponse player = playerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.ok("Jugador creado exitosamente", player));
    }

    @PutMapping("/players/{id}")
    @Operation(summary = "Actualizar jugador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Jugador actualizado"),
        @ApiResponse(responseCode = "404", description = "Jugador no encontrado")
    })
    public ResponseEntity<ApiResult<PlayerResponse>> update(
            @PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.ok(ApiResult.ok("Jugador actualizado exitosamente", playerService.update(id, request)));
    }

    @DeleteMapping("/players/{id}")
    @Operation(summary = "Eliminar jugador (logico)")
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) {
        playerService.delete(id);
        return ResponseEntity.ok(ApiResult.ok("Jugador eliminado exitosamente", null));
    }
}
