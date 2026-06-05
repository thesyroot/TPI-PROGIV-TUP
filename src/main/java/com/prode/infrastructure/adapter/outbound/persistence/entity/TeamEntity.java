package com.prode.infrastructure.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Equipo")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    private Boolean activo;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPlayerEntity> teamPlayers = new ArrayList<>();

    public TeamEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public List<TeamPlayerEntity> getTeamPlayers() { return teamPlayers; }
    public void setTeamPlayers(List<TeamPlayerEntity> teamPlayers) { this.teamPlayers = teamPlayers; }
}
