package com.prode.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Schema(description = "Request para crear o actualizar una jornada")
public class RoundRequest {

    @NotBlank(message = "El nombre de la jornada es obligatorio")
    @Schema(description = "Nombre de la jornada", example = "Fecha 1 - Fase de Grupos")
    private String nombre;

    @Schema(description = "Inicio de la jornada")
    private LocalDateTime inicioJornada;

    @Schema(description = "Fin de la jornada")
    private LocalDateTime finJornada;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDateTime getInicioJornada() { return inicioJornada; }
    public void setInicioJornada(LocalDateTime inicioJornada) { this.inicioJornada = inicioJornada; }
    public LocalDateTime getFinJornada() { return finJornada; }
    public void setFinJornada(LocalDateTime finJornada) { this.finJornada = finJornada; }
}
