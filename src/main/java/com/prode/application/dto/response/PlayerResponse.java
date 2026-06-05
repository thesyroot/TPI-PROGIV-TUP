package com.prode.application.dto.response;

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

    @Schema(description = "Rol en el equipo")
    private String rol;

    @Schema(description = "Nombre del equipo al que pertenece")
    private String equipoNombre;

    @Schema(description = "ID del equipo")
    private Long equipoId;

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
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getEquipoNombre() { return equipoNombre; }
    public void setEquipoNombre(String equipoNombre) { this.equipoNombre = equipoNombre; }
    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }
}
