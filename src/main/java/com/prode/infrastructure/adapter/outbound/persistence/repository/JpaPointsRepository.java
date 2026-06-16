package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.PointsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaPointsRepository extends JpaRepository<PointsEntity, Long> {
    Optional<PointsEntity> findByNombre(String nombre);
    Optional<PointsEntity> findByValor(Integer valor);
}
