package com.prode.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Request para crear o actualizar un partido")
public class MatchRequest {

    @NotNull(message = "El ID de la jornada es obligatorio")
    @Schema(description = "ID de la jornada", example = "1")
    private Long jornadaId;

    @NotNull(message = "La fecha del partido es obligatoria")
    @Future(message = "La fecha del partido debe ser futura")
    @Schema(description = "Fecha y hora del partido", example = "2026-06-10T20:00:00")
    private LocalDateTime fecha;

    @NotNull(message = "El ID del equipo local es obligatorio")
    @Schema(description = "ID del equipo local", example = "1")
    private Long equipoLocalId;

    @NotNull(message = "El ID del equipo visitante es obligatorio")
    @Schema(description = "ID del equipo visitante", example = "2")
    private Long equipoVisitanteId;

    public Long getJornadaId() { return jornadaId; }
    public void setJornadaId(Long jornadaId) { this.jornadaId = jornadaId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Long getEquipoLocalId() { return equipoLocalId; }
    public void setEquipoLocalId(Long equipoLocalId) { this.equipoLocalId = equipoLocalId; }
    public Long getEquipoVisitanteId() { return equipoVisitanteId; }
    public void setEquipoVisitanteId(Long equipoVisitanteId) { this.equipoVisitanteId = equipoVisitanteId; }
}
