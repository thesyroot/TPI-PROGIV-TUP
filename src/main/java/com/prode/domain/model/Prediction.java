package com.prode.domain.model;

import com.prode.domain.enums.EstadoPrediccion;
import com.prode.domain.enums.Tendencia;
import java.time.LocalDateTime;

public class Prediction {
    private Long id;
    private Match match;
    private User user;
    private Integer puntosLocal;
    private Integer puntosVisitante;
    private EstadoPrediccion estado;
    private Points points;
    private Tendencia tendencia;
    private LocalDateTime fechaCarga;
    private LocalDateTime fechaModificacion;

    public Prediction() {
        this.estado = EstadoPrediccion.ACTIVO;
        this.fechaCarga = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
    public EstadoPrediccion getEstado() { return estado; }
    public void setEstado(EstadoPrediccion estado) { this.estado = estado; }
    public Points getPoints() { return points; }
    public void setPoints(Points points) { this.points = points; }
    public Tendencia getTendencia() { return tendencia; }
    public void setTendencia(Tendencia tendencia) { this.tendencia = tendencia; }
    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
