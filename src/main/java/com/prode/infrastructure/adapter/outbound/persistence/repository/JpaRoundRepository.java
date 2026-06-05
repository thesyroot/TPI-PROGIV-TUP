package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.RoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaRoundRepository extends JpaRepository<RoundEntity, Long> {
    List<RoundEntity> findByEstado(String estado);
    boolean existsByNombre(String nombre);
    long count();
}
