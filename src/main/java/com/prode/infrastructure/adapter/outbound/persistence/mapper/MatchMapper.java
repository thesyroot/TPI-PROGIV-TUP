package com.prode.infrastructure.adapter.outbound.persistence.mapper;

import com.prode.domain.model.Match;
import com.prode.domain.enums.EstadoPartido;
import com.prode.infrastructure.adapter.outbound.persistence.entity.MatchEntity;

public class MatchMapper {

    public static Match toDomain(MatchEntity entity) {
        if (entity == null) return null;
        Match match = new Match();
        match.setId(entity.getId());
        match.setRound(RoundMapper.toDomain(entity.getJornada()));
        match.setFecha(entity.getFecha());
        match.setEquipoLocal(TeamMapper.toDomain(entity.getEquipoLocal()));
        match.setEquipoVisitante(TeamMapper.toDomain(entity.getEquipoVisitante()));
        try {
            match.setEstado(EstadoPartido.valueOf(entity.getEstado()));
        } catch (Exception e) {
            match.setEstado(EstadoPartido.POR_JUGARSE);
        }
        match.setPuntosLocal(entity.getPuntosLocal());
        match.setPuntosVisitante(entity.getPuntosVisitante());
        match.setResultado(entity.getResultado());
        return match;
    }

    public static MatchEntity toEntity(Match domain) {
        if (domain == null) return null;
        MatchEntity entity = new MatchEntity();
        entity.setId(domain.getId());
        if (domain.getRound() != null) {
            entity.setJornada(RoundMapper.toEntity(domain.getRound()));
        }
        entity.setFecha(domain.getFecha());
        if (domain.getEquipoLocal() != null) {
            entity.setEquipoLocal(TeamMapper.toEntity(domain.getEquipoLocal()));
        }
        if (domain.getEquipoVisitante() != null) {
            entity.setEquipoVisitante(TeamMapper.toEntity(domain.getEquipoVisitante()));
        }
        entity.setEstado(domain.getEstado() != null ? domain.getEstado().name() : EstadoPartido.POR_JUGARSE.name());
        entity.setPuntosLocal(domain.getPuntosLocal());
        entity.setPuntosVisitante(domain.getPuntosVisitante());
        entity.setResultado(domain.getResultado());
        return entity;
    }
}
