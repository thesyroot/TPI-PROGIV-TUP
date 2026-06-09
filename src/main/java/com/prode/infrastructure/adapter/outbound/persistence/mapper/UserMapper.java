package com.prode.infrastructure.adapter.outbound.persistence.mapper;


import com.prode.domain.enums.RolUsuario;
import com.prode.domain.model.User;
import com.prode.infrastructure.adapter.outbound.persistence.entity.UserEntity;

public class UserMapper {
    public static User toDomain(UserEntity entity){
        if (entity == null) return null;
        User user = new User();
        user.setId(entity.getId());
        user.setNombre(entity.getNombre());
        user.setApellido(entity.getApellido());
        user.setEmail(entity.getEmail());
        user.setContrasenia(entity.getContrasenia());
        user.setRol(RolUsuario.valueOf(entity.getRol()));   
        user.setPuntosTotal(entity.getPuntosTotal());
        user.setActivo(entity.getActivo());
        user.setFechaCreacion(entity.getFechaCreacion());
        return user;
    }
    public static UserEntity toEntity(User domain){
        if (domain == null)return null;
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setApellido(domain.getApellido());
        entity.setEmail(domain.getEmail());
        entity.setContrasenia(domain.getContrasenia());
        entity.setRol(domain.getRol().name());
        entity.setPuntosTotal(domain.getPuntosTotal());
        entity.setActivo(domain.getActivo());
        entity.setFechaCreacion(domain.getFechaCreacion());
        return entity;
    }
}
