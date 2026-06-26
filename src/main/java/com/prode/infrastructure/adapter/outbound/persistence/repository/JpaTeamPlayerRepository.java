package com.prode.infrastructure.adapter.outbound.persistence.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prode.infrastructure.adapter.outbound.persistence.entity.TeamPlayerEntity;

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

    @Query("SELECT tp.equipo.id, COUNT(tp) FROM TeamPlayerEntity tp WHERE tp.equipo.id IN :teamIds AND tp.activo = true GROUP BY tp.equipo.id")
    List<Object[]> countByEquipoIdsAndActivoTrue(@Param("teamIds") Collection<Long> teamIds);

    @Query("SELECT tp.jugador.id, tp.equipo.nombre FROM TeamPlayerEntity tp WHERE tp.jugador.id IN :playerIds AND tp.activo = true")
    List<Object[]> findTeamNamesByJugadorIds(@Param("playerIds") Collection<Long> playerIds);

    @Query("SELECT tp.jugador.id, tp.rol FROM TeamPlayerEntity tp WHERE tp.jugador.id IN :playerIds AND tp.activo = true")
    List<Object[]> findRolesByJugadorIds(@Param("playerIds") Collection<Long> playerIds);

    @Query(value = "SELECT e.nombre FROM Equipo e INNER JOIN EquipoXJugador ej ON e.id = ej.id_equipo WHERE ej.id_jugador = :playerId AND ej.activo = true", nativeQuery = true)
    List<String> findAllTeamNamesByPlayerId(@Param("playerId") Long playerId);

    @Query(value = "SELECT ej.rol FROM EquipoXJugador ej WHERE ej.id_jugador = :playerId AND ej.activo = true AND ej.rol IS NOT NULL", nativeQuery = true)
    List<String> findAllRolesByPlayerId(@Param("playerId") Long playerId);

    @Query(value = "SELECT ej.id_jugador as jugadorId, e.nombre as dato FROM Equipo e INNER JOIN EquipoXJugador ej ON e.id = ej.id_equipo WHERE ej.id_jugador IN :playerIds AND ej.activo = true", nativeQuery = true)
    List<PlayerDataProjection> findAllTeamNamesByPlayerIdsNative(@Param("playerIds") Set<Long> playerIds);

    @Query(value = "SELECT ej.id_jugador as jugadorId, ej.rol as dato FROM EquipoXJugador ej WHERE ej.id_jugador IN :playerIds AND ej.activo = true AND ej.rol IS NOT NULL", nativeQuery = true)
    List<PlayerDataProjection> findAllRolesByPlayerIdsNative(@Param("playerIds") Set<Long> playerIds);
}
