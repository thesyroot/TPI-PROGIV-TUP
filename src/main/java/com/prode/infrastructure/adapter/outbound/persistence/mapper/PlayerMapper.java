package com.prode.infrastructure.adapter.outbound.persistence.mapper;

import com.prode.domain.model.Player;
import com.prode.infrastructure.adapter.outbound.persistence.entity.PlayerEntity;

public class PlayerMapper {

    public static Player toDomain(PlayerEntity entity) {
        if (entity == null) return null;
        Player player = new Player();
        player.setId(entity.getId());
        player.setNombre(entity.getNombre());
        player.setApellido(entity.getApellido());
        player.setNumeroCamiseta(entity.getNumeroCamiseta());
        player.setImagenUrl(entity.getImagenUrl());
        player.setActivo(entity.getActivo());
        return player;
    }

    public static PlayerEntity toEntity(Player domain) {
        if (domain == null) return null;
        PlayerEntity entity = new PlayerEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setApellido(domain.getApellido());
        entity.setNumeroCamiseta(domain.getNumeroCamiseta());
        entity.setImagenUrl(domain.getImagenUrl());
        entity.setActivo(domain.getActivo());
        return entity;
    }
}
