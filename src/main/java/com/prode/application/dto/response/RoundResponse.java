package com.prode.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con datos de una jornada")
public class RoundResponse {

    @Schema(description = "ID de la jornada")
    private Long id;

    @Schema(description = "Nombre de la jornada")
    private String nombre;

    @Schema(description = "Inicio de la jornada")
    private LocalDateTime inicioJornada;

    @Schema(description = "Fin de la jornada")
    private LocalDateTime finJornada;

    @Schema(description = "Estado de la jornada", example = "PROGRAMADA")
    private String estado;

    @Schema(description = "Cantidad de partidos")
    private int cantidadPartidos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDateTime getInicioJornada() { return inicioJornada; }
    public void setInicioJornada(LocalDateTime inicioJornada) { this.inicioJornada = inicioJornada; }
    public LocalDateTime getFinJornada() { return finJornada; }
    public void setFinJornada(LocalDateTime finJornada) { this.finJornada = finJornada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getCantidadPartidos() { return cantidadPartidos; }
    public void setCantidadPartidos(int cantidadPartidos) { this.cantidadPartidos = cantidadPartidos; }
}
