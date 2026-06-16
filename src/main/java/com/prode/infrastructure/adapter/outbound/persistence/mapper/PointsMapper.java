package com.prode.infrastructure.adapter.outbound.persistence.mapper;

import com.prode.domain.model.Points;
import com.prode.infrastructure.adapter.outbound.persistence.entity.PointsEntity;

public class PointsMapper {

    public static Points toDomain(PointsEntity entity) {
        if (entity == null) return null;
        Points points = new Points();
        points.setId(entity.getId());
        points.setNombre(entity.getNombre());
        points.setValor(entity.getValor());
        points.setActivo(entity.getActivo());
        return points;
    }

    public static PointsEntity toEntity(Points domain) {
        if (domain == null) return null;
        PointsEntity entity = new PointsEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setValor(domain.getValor());
        entity.setActivo(domain.getActivo());
        return entity;
    }
}