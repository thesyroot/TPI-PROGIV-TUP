package com.prode.domain.model;

import com.prode.domain.enums.RolUsuario;
import java.time.LocalDateTime;

public class User {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasenia;
    private RolUsuario rol;
    private Integer puntosTotal;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    public User() {}

    public User(Long id, String nombre, String apellido, String email, String contrasenia, RolUsuario rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.puntosTotal = 0;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    public Integer getPuntosTotal() { return puntosTotal; }
    public void setPuntosTotal(Integer puntosTotal) { this.puntosTotal = puntosTotal; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
