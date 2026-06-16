package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.domain.model.Points;
import com.prode.domain.port.outbound.PointsRepository;
import com.prode.infrastructure.adapter.outbound.persistence.mapper.PointsMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class PointsRepositoryAdapter implements PointsRepository {

    private final JpaPointsRepository jpaPointsRepository;

    public PointsRepositoryAdapter(JpaPointsRepository jpaPointsRepository) {
        this.jpaPointsRepository = jpaPointsRepository;
    }

    @Override
    public Optional<Points> findByValor(Integer valor) {
        return jpaPointsRepository.findByValor(valor).map(PointsMapper::toDomain);
    }

    @Override
    public Points save(Points points) {
        return PointsMapper.toDomain(jpaPointsRepository.save(PointsMapper.toEntity(points)));
    }
}
