package com.prode.infrastructure.adapter.outbound.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prode.infrastructure.adapter.outbound.persistence.entity.TeamEntity;

public interface JpaTeamRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findByActivoTrue();

    Page<TeamEntity> findByActivoTrue(Pageable pageable);

    boolean existsByNombreAndRoundId(String nombre, Long roundId);

    List<TeamEntity> findByRoundId(Long roundId);

    Page<TeamEntity> findByRoundId(Long roundId, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM MatchEntity m WHERE m.equipoLocal.id = :teamId OR m.equipoVisitante.id = :teamId")
    boolean existsMatchWithTeam(@Param("teamId") Long teamId);

    @Query("SELECT t FROM TeamEntity t WHERE t.activo = true " +
            "AND (:nombre IS NULL OR :nombre = '' OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:roundNombre IS NULL OR :roundNombre = '' OR t.roundId IN (SELECT r.id FROM RoundEntity r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :roundNombre, '%'))))")
    Page<TeamEntity> searchTeams(@Param("nombre") String nombre, @Param("roundNombre") String roundNombre,
            Pageable pageable);
}
