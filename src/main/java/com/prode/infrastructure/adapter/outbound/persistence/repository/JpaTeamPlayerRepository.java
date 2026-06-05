package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.TeamPlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface JpaTeamPlayerRepository extends JpaRepository<TeamPlayerEntity, Long> {
    List<TeamPlayerEntity> findByEquipoIdAndActivoTrue(Long equipoId);
    Optional<TeamPlayerEntity> findByEquipoIdAndJugadorId(Long equipoId, Long jugadorId);
    boolean existsByEquipoIdAndJugadorId(Long equipoId, Long jugadorId);

    @Query("SELECT COUNT(tp) FROM TeamPlayerEntity tp WHERE tp.equipo.id = :teamId AND tp.activo = true")
    int countByEquipoIdAndActivoTrue(@Param("teamId") Long teamId);

    @Query("SELECT tp.equipo.nombre FROM TeamPlayerEntity tp WHERE tp.jugador.id = :playerId AND tp.activo = true")
    String findTeamNameByJugadorId(@Param("playerId") Long playerId);

    @Query("SELECT tp.rol FROM TeamPlayerEntity tp WHERE tp.jugador.id = :playerId AND tp.activo = true")
    String findRolByJugadorId(@Param("playerId") Long playerId);

    @Query("SELECT tp.jugador FROM TeamPlayerEntity tp WHERE tp.activo = true")
    List<com.prode.infrastructure.adapter.outbound.persistence.entity.PlayerEntity> findAllAssignedPlayers();
}
