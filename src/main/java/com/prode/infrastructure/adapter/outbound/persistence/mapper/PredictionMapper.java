package com.prode.infrastructure.adapter.outbound.persistence.mapper;

import com.prode.domain.enums.EstadoPrediccion;
import com.prode.domain.enums.Tendencia;
import com.prode.domain.model.Prediction;
import com.prode.infrastructure.adapter.outbound.persistence.entity.PredictionEntity;

public class PredictionMapper {

    public static Prediction toDomain(PredictionEntity entity) {
        if (entity == null) return null;
        Prediction prediction = new Prediction();
        prediction.setId(entity.getId());
        prediction.setMatch(MatchMapper.toDomain(entity.getPartido()));
        prediction.setUser(UserMapper.toDomain(entity.getUsuario()));
        prediction.setPuntosLocal(entity.getPuntosLocal());
        prediction.setPuntosVisitante(entity.getPuntosVisitante());
        if (entity.getEstado() != null)
            prediction.setEstado(EstadoPrediccion.valueOf(entity.getEstado()));
        if (entity.getPuntosAsignados() != null) {
            prediction.setPoints(PointsMapper.toDomain(entity.getPuntosAsignados()));
        }
        if (entity.getTendencia() != null)
            prediction.setTendencia(Tendencia.valueOf(entity.getTendencia()));
        prediction.setFechaCarga(entity.getFechaCarga());
        prediction.setFechaModificacion(entity.getFechaModificacion());
        return prediction;
    }

    public static PredictionEntity toEntity(Prediction domain) {
        if (domain == null) return null;
        PredictionEntity entity = new PredictionEntity();
        entity.setId(domain.getId());
        if (domain.getMatch() != null)
            entity.setPartido(MatchMapper.toEntity(domain.getMatch()));
        if (domain.getUser() != null)
            entity.setUsuario(UserMapper.toEntity(domain.getUser()));
        entity.setPuntosLocal(domain.getPuntosLocal());
        entity.setPuntosVisitante(domain.getPuntosVisitante());
        if (domain.getEstado() != null)
            entity.setEstado(domain.getEstado().name());
        if (domain.getPoints() != null)
            entity.setPuntosAsignados(PointsMapper.toEntity(domain.getPoints()));
        if (domain.getTendencia() != null)
            entity.setTendencia(domain.getTendencia().name());
        entity.setFechaCarga(domain.getFechaCarga());
        entity.setFechaModificacion(domain.getFechaModificacion());
        return entity;
    }
}