package com.prode.infrastructure.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Partido")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jornada", nullable = false)
    private RoundEntity jornada;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_local", nullable = false)
    private TeamEntity equipoLocal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo_visitante", nullable = false)
    private TeamEntity equipoVisitante;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "puntos_local")
    private Integer puntosLocal;

    @Column(name = "puntos_visitante")
    private Integer puntosVisitante;

    @Column(length = 20)
    private String resultado;

    public MatchEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RoundEntity getJornada() { return jornada; }
    public void setJornada(RoundEntity jornada) { this.jornada = jornada; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public TeamEntity getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(TeamEntity equipoLocal) { this.equipoLocal = equipoLocal; }
    public TeamEntity getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(TeamEntity equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}
