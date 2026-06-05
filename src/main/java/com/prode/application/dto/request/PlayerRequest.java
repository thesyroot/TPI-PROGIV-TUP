package com.prode.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request para crear o actualizar un jugador")
public class PlayerRequest {

    @NotBlank(message = "El nombre del jugador es obligatorio")
    @Schema(description = "Nombre del jugador", example = "Lionel")
    private String nombre;

    @NotBlank(message = "El apellido del jugador es obligatorio")
    @Schema(description = "Apellido del jugador", example = "Messi")
    private String apellido;

    @Schema(description = "Numero de camiseta", example = "10")
    private Integer numeroCamiseta;

    @Schema(description = "ID del equipo al que pertenece")
    private Long equipoId;

    @Schema(description = "Rol del jugador en el equipo", example = "DELANTERO")
    private String rol;

    @Schema(description = "URL de la imagen del jugador")
    private String imagenUrl;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public Integer getNumeroCamiseta() { return numeroCamiseta; }
    public void setNumeroCamiseta(Integer numeroCamiseta) { this.numeroCamiseta = numeroCamiseta; }
    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
