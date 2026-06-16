package com.prode.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class MatchResultRequest {

    @NotNull(message = "Home team score is required")
    @PositiveOrZero(message = "Home team score must be zero or positive")
    private Integer puntosLocal;

    @NotNull(message = "Away team score is required")
    @PositiveOrZero(message = "Away team score must be zero or positive")
    private Integer puntosVisitante;

    public MatchResultRequest() {}

    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
}