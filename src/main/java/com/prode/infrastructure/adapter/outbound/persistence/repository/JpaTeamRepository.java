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

    @Query("SELECT COUNT(m) > 0 FROM MatchEntity m WHERE m.equipoLocal.id = :teamId OR m.equipoVisitante.id = :teamId")
    boolean existsMatchWithTeam(@Param("teamId") Long teamId);
}
