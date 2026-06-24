package com.prode.infrastructure.adapter.outbound.persistence.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prode.infrastructure.adapter.outbound.persistence.entity.MatchEntity;

public interface JpaMatchRepository extends JpaRepository<MatchEntity, Long> {
       List<MatchEntity> findByJornadaIdOrderByFechaAsc(Long jornadaId);

       Page<MatchEntity> findByJornadaIdOrderByFechaAsc(Long jornadaId, Pageable pageable);

       List<MatchEntity> findByEstado(String estado);

       List<MatchEntity> findAllByOrderByFechaAsc();

       Page<MatchEntity> findAllByOrderByFechaAsc(Pageable pageable);

       // Para contar la cantidad de partido que ocurren despues de la fecha
       long countByFechaAfter(java.time.LocalDateTime fecha);

       @Query("SELECT COUNT(p) FROM PredictionEntity p WHERE p.partido.id = :matchId")
       long countPredictionsByMatchId(Long matchId);

       long countByJornadaId(Long jornadaId);

       long countByJornadaIdAndEstado(Long jornadaId, String estado);

       @Query("SELECT m.jornada.id, COUNT(m) FROM MatchEntity m WHERE m.jornada.id IN :roundIds GROUP BY m.jornada.id")
       List<Object[]> countByJornadaIdIn(@Param("roundIds") Collection<Long> roundIds);

       @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MatchEntity m WHERE m.equipoLocal.id = :teamId OR m.equipoVisitante.id = :teamId")
       boolean existsMatchWithTeam(Long teamId);

       @Query("SELECT m FROM MatchEntity m JOIN FETCH m.jornada JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante ORDER BY m.fecha ASC")
       List<MatchEntity> findAllWithRelationsByOrderByFechaAsc();

    List<MatchEntity> findAllWithRelationsByOrderByFechaAsc();
       Page<MatchEntity> findAllWithRelationsByOrderByFechaAsc(Pageable pageable);

       @Query("SELECT m FROM MatchEntity m JOIN FETCH m.jornada JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante WHERE m.jornada.id = :jornadaId ORDER BY m.fecha ASC")
       List<MatchEntity> findByJornadaIdWithRelations(@Param("jornadaId") Long jornadaId);

       @Query(value = "SELECT m FROM MatchEntity m JOIN FETCH m.jornada JOIN FETCH m.equipoLocal JOIN FETCH m.equipoVisitante WHERE m.jornada.id = :jornadaId ORDER BY m.fecha ASC", countQuery = "SELECT COUNT(m) FROM MatchEntity m WHERE m.jornada.id = :jornadaId")
       Page<MatchEntity> findByJornadaIdWithRelations(@Param("jornadaId") Long jornadaId, Pageable pageable);

}
