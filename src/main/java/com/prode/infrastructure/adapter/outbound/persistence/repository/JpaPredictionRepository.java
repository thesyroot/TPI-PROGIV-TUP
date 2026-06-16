package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.PredictionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface JpaPredictionRepository extends JpaRepository<PredictionEntity, Long> {

    @Query("SELECT p FROM PredictionEntity p JOIN FETCH p.partido m JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante JOIN FETCH p.usuario WHERE p.usuario.id = :usuarioId ORDER BY m.fecha ASC, p.id ASC")
    List<PredictionEntity> findByUsuarioIdWithRelations(@Param("usuarioId") Long usuarioId);

    @Query("SELECT p FROM PredictionEntity p JOIN FETCH p.partido m JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante JOIN FETCH p.usuario WHERE p.partido.id = :partidoId ORDER BY p.usuario.id")
    List<PredictionEntity> findByPartidoIdWithRelations(@Param("partidoId") Long partidoId);

    @Query("SELECT p FROM PredictionEntity p JOIN FETCH p.partido m JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante JOIN FETCH p.usuario WHERE p.usuario.id = :usuarioId AND p.partido.id = :partidoId")
    Optional<PredictionEntity> findByUsuarioIdAndPartidoId(@Param("usuarioId") Long usuarioId, @Param("partidoId") Long partidoId);

    boolean existsByUsuarioIdAndPartidoId(Long usuarioId, Long partidoId);

    @Query("SELECT p FROM PredictionEntity p JOIN FETCH p.partido m JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante JOIN FETCH p.usuario")
    List<PredictionEntity> findAllWithRelations();

    @Query(value = "SELECT p FROM PredictionEntity p JOIN FETCH p.partido m JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante JOIN FETCH p.usuario WHERE (:matchId IS NULL OR m.id = :matchId) AND (:jornadaId IS NULL OR m.jornada.id = :jornadaId) AND (:usuarioId IS NULL OR p.usuario.id = :usuarioId) ORDER BY m.fecha ASC, p.id ASC",
           countQuery = "SELECT COUNT(p) FROM PredictionEntity p JOIN p.partido m WHERE (:matchId IS NULL OR m.id = :matchId) AND (:jornadaId IS NULL OR m.jornada.id = :jornadaId) AND (:usuarioId IS NULL OR p.usuario.id = :usuarioId)")
    Page<PredictionEntity> findAllFiltered(@Param("matchId") Long matchId,
                                           @Param("jornadaId") Long jornadaId,
                                           @Param("usuarioId") Long usuarioId,
                                           Pageable pageable);
}