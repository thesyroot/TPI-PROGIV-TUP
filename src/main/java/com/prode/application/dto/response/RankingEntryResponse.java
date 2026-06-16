package com.prode.application.dto.response;

public class RankingEntryResponse {
    private Integer posicion;
    private Long userId;
    private String nombre;
    private String apellido;
    private Integer puntosTotal;
    private Long plenos;          
    private Long aciertos;        

    public RankingEntryResponse() {}

    public Integer getPosicion() { return posicion; }
    public void setPosicion(Integer posicion) { this.posicion = posicion; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public Integer getPuntosTotal() { return puntosTotal; }
    public void setPuntosTotal(Integer puntosTotal) { this.puntosTotal = puntosTotal; }
    public Long getPlenos() { return plenos; }
    public void setPlenos(Long plenos) { this.plenos = plenos; }
    public Long getAciertos() { return aciertos; }
    public void setAciertos(Long aciertos) { this.aciertos = aciertos; }
}
