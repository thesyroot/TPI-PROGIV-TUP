package com.prode.domain.model;

import com.prode.domain.enums.EstadoPartido;
import java.time.LocalDateTime;

public class Match {
    private Long id;
    private Round round;
    private LocalDateTime fecha;
    private Team equipoLocal;
    private Team equipoVisitante;
    private EstadoPartido estado;
    private Integer puntosLocal;
    private Integer puntosVisitante;
    private String resultado;

    public Match() {
        this.estado = EstadoPartido.POR_JUGARSE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Round getRound() { return round; }
    public void setRound(Round round) { this.round = round; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Team getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Team equipoLocal) { this.equipoLocal = equipoLocal; }
    public Team getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(Team equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    public EstadoPartido getEstado() { return estado; }
    public void setEstado(EstadoPartido estado) { this.estado = estado; }
    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}
