package com.prode.domain.model;

public class Points {
    private Long id;
    private String nombre;
    private Integer valor;
    private Boolean activo;

    public Points() {}

    public Points(Long id, String nombre, Integer valor) {
        this.id = id;
        this.nombre = nombre;
        this.valor = valor;
        this.activo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getValor() { return valor; }
    public void setValor(Integer valor) { this.valor = valor; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
