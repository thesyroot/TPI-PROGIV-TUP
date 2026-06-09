package com.prode.application.dto.response;

import com.prode.domain.enums.RolUsuario;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con datos del usuario")
public class UserResponse {

    private Long id;

    private String nombre;

    private String apellido;

    private String email;

    private RolUsuario rol;

    private Integer puntosTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

     public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public Integer getPuntosTotal() {
        return puntosTotal;
    }

    public void setPuntosTotal(Integer puntosTotal) {
        this.puntosTotal = puntosTotal;
    }
}
