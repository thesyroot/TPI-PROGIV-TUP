package com.prode.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Respuesta con datos de un equipo")
public class TeamResponse {

    @Schema(description = "ID del equipo")
    private Long id;

    @Schema(description = "Nombre del equipo")
    private String nombre;

    @Schema(description = "URL de la imagen/escudo")
    private String imagenUrl;

    @Schema(description = "Cantidad de jugadores")
    private int cantidadJugadores;

    @Schema(description = "Jugadores del equipo")
    private List<PlayerResponse> jugadores;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public int getCantidadJugadores() { return cantidadJugadores; }
    public void setCantidadJugadores(int cantidadJugadores) { this.cantidadJugadores = cantidadJugadores; }
    public List<PlayerResponse> getJugadores() { return jugadores; }
    public void setJugadores(List<PlayerResponse> jugadores) { this.jugadores = jugadores; }
}
