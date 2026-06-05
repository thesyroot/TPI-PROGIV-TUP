package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.PlayerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JpaPlayerRepository extends JpaRepository<PlayerEntity, Long> {
    List<PlayerEntity> findByActivoTrue();
    Page<PlayerEntity> findByActivoTrue(Pageable pageable);
    List<PlayerEntity> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT tp.jugador FROM TeamPlayerEntity tp WHERE tp.equipo.id = :teamId AND tp.activo = true")
    List<PlayerEntity> findActiveByTeamId(Long teamId);

    @Query("SELECT p FROM PlayerEntity p WHERE p.activo = true AND p.id NOT IN (SELECT tp.jugador.id FROM TeamPlayerEntity tp WHERE tp.activo = true)")
    List<PlayerEntity> findUnassigned();

    @Query("SELECT p FROM PlayerEntity p WHERE p.activo = true AND (p.id NOT IN (SELECT tp.jugador.id FROM TeamPlayerEntity tp WHERE tp.activo = true) OR p.id IN (SELECT tp.jugador.id FROM TeamPlayerEntity tp WHERE tp.equipo.id = :teamId AND tp.activo = true))")
    List<PlayerEntity> findUnassignedOrByTeamId(@Param("teamId") Long teamId);
}
