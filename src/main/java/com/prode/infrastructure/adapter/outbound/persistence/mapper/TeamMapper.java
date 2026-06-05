package com.prode.infrastructure.adapter.outbound.persistence.mapper;

import com.prode.domain.model.Team;
import com.prode.infrastructure.adapter.outbound.persistence.entity.TeamEntity;

public class TeamMapper {

    public static Team toDomain(TeamEntity entity) {
        if (entity == null) return null;
        Team team = new Team();
        team.setId(entity.getId());
        team.setNombre(entity.getNombre());
        team.setActivo(entity.getActivo());
        team.setImagenUrl(entity.getImagenUrl());
        return team;
    }

    public static TeamEntity toEntity(Team domain) {
        if (domain == null) return null;
        TeamEntity entity = new TeamEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setActivo(domain.getActivo());
        entity.setImagenUrl(domain.getImagenUrl());
        return entity;
    }
}
