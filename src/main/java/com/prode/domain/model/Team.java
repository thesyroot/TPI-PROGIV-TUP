package com.prode.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private Long id;
    private String nombre;
    private Boolean activo;
    private String imagenUrl;
    private List<Player> players;

    public Team() {
        this.activo = true;
        this.players = new ArrayList<>();
    }

    public Team(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.activo = true;
        this.players = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
}
