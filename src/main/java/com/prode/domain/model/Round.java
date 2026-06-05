package com.prode.domain.model;

import com.prode.domain.enums.EstadoJornada;
import java.time.LocalDateTime;

public class Round {
    private Long id;
    private String nombre;
    private LocalDateTime inicioJornada;
    private LocalDateTime finJornada;
    private EstadoJornada estado;

    public Round() {
        this.estado = EstadoJornada.PROGRAMADA;
    }

    public Round(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.estado = EstadoJornada.PROGRAMADA;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDateTime getInicioJornada() { return inicioJornada; }
    public void setInicioJornada(LocalDateTime inicioJornada) { this.inicioJornada = inicioJornada; }
    public LocalDateTime getFinJornada() { return finJornada; }
    public void setFinJornada(LocalDateTime finJornada) { this.finJornada = finJornada; }
    public EstadoJornada getEstado() { return estado; }
    public void setEstado(EstadoJornada estado) { this.estado = estado; }
}
