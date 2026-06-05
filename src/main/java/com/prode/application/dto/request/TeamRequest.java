package com.prode.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Schema(description = "Request para crear o actualizar un equipo")
public class TeamRequest {

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Schema(description = "Nombre del equipo", example = "Boca Juniors")
    private String nombre;

    @Schema(description = "URL de la imagen/escudo del equipo")
    private String imagenUrl;

    @Schema(description = "Roles de jugadores asignados al equipo (playerId -> rol)")
    private Map<Long, String> roles = new HashMap<>();

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Map<Long, String> getRoles() { return roles; }
    public void setRoles(Map<Long, String> roles) { this.roles = roles; }
}
