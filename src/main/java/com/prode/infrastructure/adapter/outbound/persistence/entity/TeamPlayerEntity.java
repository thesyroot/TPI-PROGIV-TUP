package com.prode.infrastructure.adapter.outbound.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "EquipoXJugador", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_equipo", "id_jugador"})
})
public class TeamPlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private TeamEntity equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jugador", nullable = false)
    private PlayerEntity jugador;

    @Column(length = 30)
    private String rol;

    private Boolean activo;

    public TeamPlayerEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TeamEntity getEquipo() { return equipo; }
    public void setEquipo(TeamEntity equipo) { this.equipo = equipo; }
    public PlayerEntity getJugador() { return jugador; }
    public void setJugador(PlayerEntity jugador) { this.jugador = jugador; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
