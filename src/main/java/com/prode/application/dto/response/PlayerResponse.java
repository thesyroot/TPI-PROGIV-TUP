package com.prode.application.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con datos de un jugador")
public class PlayerResponse {

    @Schema(description = "ID del jugador")
    private Long id;

    @Schema(description = "Nombre del jugador")
    private String nombre;

    @Schema(description = "Apellido del jugador")
    private String apellido;

    @Schema(description = "Numero de camiseta")
    private Integer numeroCamiseta;

    @Schema(description = "URL de la imagen")
    private String imagenUrl;

    @Schema(description = "Posición más popular del jugador en todos sus equipos")
    private String posicionPopular;

    @Schema(description = "Lista de equipos únicos en los que jugó")
    private List<String> equipos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public Integer getNumeroCamiseta() { return numeroCamiseta; }
    public void setNumeroCamiseta(Integer numeroCamiseta) { this.numeroCamiseta = numeroCamiseta; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getPosicionPopular() { return posicionPopular; }
    public void setPosicionPopular(String posicionPopular) { this.posicionPopular = posicionPopular; }
    public List<String> getEquipos() { return equipos; }
    public void setEquipos(List<String> equipos) { this.equipos = equipos; }
}