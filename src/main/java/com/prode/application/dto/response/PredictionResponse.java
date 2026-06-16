package com.prode.application.dto.response;

import com.prode.domain.enums.EstadoPrediccion;
import com.prode.domain.enums.Tendencia;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Prediction response")
public class PredictionResponse {

    @Schema(description = "Prediction ID", example = "1")
    private Long id;

    @Schema(description = "Match ID", example = "1")
    private Long matchId;

    @Schema(description = "Home team name", example = "Argentina")
    private String local;

    @Schema(description = "Home team image URL")
    private String localImagen;

    @Schema(description = "Away team name", example = "Brasil")
    private String visitante;

    @Schema(description = "Away team image URL")
    private String visitanteImagen;

    @Schema(description = "Match date and time")
    private LocalDateTime fechaPartido;

    @Schema(description = "Round number", example = "1")
    private String jornada;

    @Schema(description = "Predicted home team score", example = "2")
    private Integer puntosLocal;

    @Schema(description = "Predicted away team score", example = "1")
    private Integer puntosVisitante;

    @Schema(description = "Prediction status")
    private EstadoPrediccion estado;

    @Schema(description = "Predicted tendency")
    private Tendencia tendencia;

    @Schema(description = "User ID who made the prediction", example = "1")
    private Long userId;

    @Schema(description = "User full name", example = "Juan Pérez")
    private String userNombre;

    @Schema(description = "Creation date")
    private LocalDateTime fechaCarga;

    @Schema(description = "Last modification date")
    private LocalDateTime fechaModificacion;

    @Schema(description = "Points awarded for this prediction (null if not resolved yet)")
    private Integer puntosObtenidos;

    public PredictionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getLocalImagen() { return localImagen; }
    public void setLocalImagen(String localImagen) { this.localImagen = localImagen; }
    public String getVisitante() { return visitante; }
    public void setVisitante(String visitante) { this.visitante = visitante; }
    public String getVisitanteImagen() { return visitanteImagen; }
    public void setVisitanteImagen(String visitanteImagen) { this.visitanteImagen = visitanteImagen; }
    public LocalDateTime getFechaPartido() { return fechaPartido; }
    public void setFechaPartido(LocalDateTime fechaPartido) { this.fechaPartido = fechaPartido; }
    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }
    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
    public EstadoPrediccion getEstado() { return estado; }
    public void setEstado(EstadoPrediccion estado) { this.estado = estado; }
    public Tendencia getTendencia() { return tendencia; }
    public void setTendencia(Tendencia tendencia) { this.tendencia = tendencia; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserNombre() { return userNombre; }
    public void setUserNombre(String userNombre) { this.userNombre = userNombre; }
    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
    public Integer getPuntosObtenidos() { return puntosObtenidos; }
    public void setPuntosObtenidos(Integer puntosObtenidos) { this.puntosObtenidos = puntosObtenidos; }
}
