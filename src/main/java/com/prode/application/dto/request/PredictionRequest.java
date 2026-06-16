package com.prode.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Request body for creating or updating a prediction")
public class PredictionRequest {

    @NotNull(message = "Match ID is required")
    @Schema(description = "Match ID", example = "1")
    private Long matchId;

    @NotNull(message = "Home team score is required")
    @PositiveOrZero(message = "Home team score must be zero or positive")
    @Schema(description = "Predicted home team score", example = "2")
    private Integer puntosLocal;

    @NotNull(message = "Away team score is required")
    @PositiveOrZero(message = "Away team score must be zero or positive")
    @Schema(description = "Predicted away team score", example = "1")
    private Integer puntosVisitante;

    public PredictionRequest() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
}