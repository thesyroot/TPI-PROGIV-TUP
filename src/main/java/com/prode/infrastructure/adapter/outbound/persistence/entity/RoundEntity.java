package com.prode.infrastructure.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Jornada")
public class RoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String nombre;

    @Column(name = "inicio_jornada")
    private LocalDateTime inicioJornada;

    @Column(name = "fin_jornada")
    private LocalDateTime finJornada;

    @Column(nullable = false, length = 20)
    private String estado;

    public RoundEntity() {}

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
}
