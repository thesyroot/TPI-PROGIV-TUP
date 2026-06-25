package com.prode.application.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con datos de un equipo")
public class TeamResponse {

    @Schema(description = "ID del equipo")
    private Long id;

    @Schema(description = "Nombre del equipo")
    private String nombre;
    

    @Schema(description = "URL de la imagen/escudo")
    private String imagenUrl;

    @Schema(description = "ID de la temporada") 
    private Long roundId;

    @Schema(description = "Nombre de la jornada") // <-- NUEVO
    private String roundNombre;

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
    public Long getRoundId() { return roundId; }
    public void setRoundId(Long roundId) { this.roundId = roundId; }
    public String getRoundNombre() { return roundNombre; }
    public void setRoundNombre(String roundNombre) { this.roundNombre = roundNombre; }
}
