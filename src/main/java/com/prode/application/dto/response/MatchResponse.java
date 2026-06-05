package com.prode.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con datos de un partido")
public class MatchResponse {

    @Schema(description = "ID del partido")
    private Long id;

    @Schema(description = "ID de la jornada")
    private Long jornadaId;

    @Schema(description = "Nombre de la jornada")
    private String jornadaNombre;

    @Schema(description = "Fecha y hora del partido")
    private LocalDateTime fecha;

    @Schema(description = "ID del equipo local")
    private Long equipoLocalId;

    @Schema(description = "Nombre del equipo local")
    private String equipoLocalNombre;

    @Schema(description = "Escudo del equipo local")
    private String equipoLocalImagen;

    @Schema(description = "ID del equipo visitante")
    private Long equipoVisitanteId;

    @Schema(description = "Nombre del equipo visitante")
    private String equipoVisitanteNombre;

    @Schema(description = "Escudo del equipo visitante")
    private String equipoVisitanteImagen;

    @Schema(description = "Estado del partido", example = "POR_JUGARSE")
    private String estado;

    @Schema(description = "Goles del equipo local (resultado real)")
    private Integer puntosLocal;

    @Schema(description = "Goles del equipo visitante (resultado real)")
    private Integer puntosVisitante;

    @Schema(description = "Resultado", example = "LOCAL")
    private String resultado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJornadaId() { return jornadaId; }
    public void setJornadaId(Long jornadaId) { this.jornadaId = jornadaId; }
    public String getJornadaNombre() { return jornadaNombre; }
    public void setJornadaNombre(String jornadaNombre) { this.jornadaNombre = jornadaNombre; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Long getEquipoLocalId() { return equipoLocalId; }
    public void setEquipoLocalId(Long equipoLocalId) { this.equipoLocalId = equipoLocalId; }
    public String getEquipoLocalNombre() { return equipoLocalNombre; }
    public void setEquipoLocalNombre(String equipoLocalNombre) { this.equipoLocalNombre = equipoLocalNombre; }
    public String getEquipoLocalImagen() { return equipoLocalImagen; }
    public void setEquipoLocalImagen(String equipoLocalImagen) { this.equipoLocalImagen = equipoLocalImagen; }
    public Long getEquipoVisitanteId() { return equipoVisitanteId; }
    public void setEquipoVisitanteId(Long equipoVisitanteId) { this.equipoVisitanteId = equipoVisitanteId; }
    public String getEquipoVisitanteNombre() { return equipoVisitanteNombre; }
    public void setEquipoVisitanteNombre(String equipoVisitanteNombre) { this.equipoVisitanteNombre = equipoVisitanteNombre; }
    public String getEquipoVisitanteImagen() { return equipoVisitanteImagen; }
    public void setEquipoVisitanteImagen(String equipoVisitanteImagen) { this.equipoVisitanteImagen = equipoVisitanteImagen; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}
