package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.PredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaPredictionRepository extends JpaRepository<PredictionEntity, Long> {
    List<PredictionEntity> findByUsuarioId(Long usuarioId);
    List<PredictionEntity> findByPartidoId(Long partidoId);
    boolean existsByUsuarioIdAndPartidoId(Long usuarioId, Long partidoId);
}
