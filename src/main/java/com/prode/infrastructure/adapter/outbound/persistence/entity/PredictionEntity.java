package com.prode.infrastructure.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Prediccion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_partido", "id_usuario"})
})
public class PredictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partido", nullable = false)
    private MatchEntity partido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UserEntity usuario;

    @Column(name = "puntos_local", nullable = false)
    private Integer puntosLocal;

    @Column(name = "puntos_visitante", nullable = false)
    private Integer puntosVisitante;

    @Column(nullable = false, length = 20)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puntos_asignados")
    private PointsEntity puntosAsignados;

    @Column(length = 20)
    private String tendencia;

    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public PredictionEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MatchEntity getPartido() { return partido; }
    public void setPartido(MatchEntity partido) { this.partido = partido; }
    public UserEntity getUsuario() { return usuario; }
    public void setUsuario(UserEntity usuario) { this.usuario = usuario; }
    public Integer getPuntosLocal() { return puntosLocal; }
    public void setPuntosLocal(Integer puntosLocal) { this.puntosLocal = puntosLocal; }
    public Integer getPuntosVisitante() { return puntosVisitante; }
    public void setPuntosVisitante(Integer puntosVisitante) { this.puntosVisitante = puntosVisitante; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public PointsEntity getPuntosAsignados() { return puntosAsignados; }
    public void setPuntosAsignados(PointsEntity puntosAsignados) { this.puntosAsignados = puntosAsignados; }
    public String getTendencia() { return tendencia; }
    public void setTendencia(String tendencia) { this.tendencia = tendencia; }
    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
