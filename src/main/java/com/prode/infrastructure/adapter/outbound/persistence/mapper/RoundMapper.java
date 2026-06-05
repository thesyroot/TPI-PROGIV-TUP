package com.prode.infrastructure.adapter.outbound.persistence.mapper;

import com.prode.domain.model.Round;
import com.prode.domain.enums.EstadoJornada;
import com.prode.infrastructure.adapter.outbound.persistence.entity.RoundEntity;

public class RoundMapper {

    public static Round toDomain(RoundEntity entity) {
        if (entity == null) return null;
        Round round = new Round();
        round.setId(entity.getId());
        round.setNombre(entity.getNombre());
        round.setInicioJornada(entity.getInicioJornada());
        round.setFinJornada(entity.getFinJornada());
        try {
            round.setEstado(EstadoJornada.valueOf(entity.getEstado()));
        } catch (Exception e) {
            round.setEstado(EstadoJornada.PROGRAMADA);
        }
        return round;
    }

    public static RoundEntity toEntity(Round domain) {
        if (domain == null) return null;
        RoundEntity entity = new RoundEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setInicioJornada(domain.getInicioJornada());
        entity.setFinJornada(domain.getFinJornada());
        entity.setEstado(domain.getEstado() != null ? domain.getEstado().name() : EstadoJornada.PROGRAMADA.name());
        return entity;
    }
}
