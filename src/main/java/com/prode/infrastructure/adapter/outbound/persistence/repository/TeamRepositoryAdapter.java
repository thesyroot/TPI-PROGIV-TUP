package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.domain.model.Team;
import com.prode.domain.port.outbound.TeamRepository;
import com.prode.infrastructure.adapter.outbound.persistence.entity.PlayerEntity;
import com.prode.infrastructure.adapter.outbound.persistence.entity.TeamEntity;
import com.prode.infrastructure.adapter.outbound.persistence.entity.TeamPlayerEntity;
import com.prode.infrastructure.adapter.outbound.persistence.mapper.TeamMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TeamRepositoryAdapter implements TeamRepository {

    private final JpaTeamRepository jpaTeamRepository;
    private final JpaTeamPlayerRepository jpaTeamPlayerRepository;
    private final JpaPlayerRepository jpaPlayerRepository;

    public TeamRepositoryAdapter(JpaTeamRepository jpaTeamRepository,
                                  JpaTeamPlayerRepository jpaTeamPlayerRepository,
                                  JpaPlayerRepository jpaPlayerRepository) {
        this.jpaTeamRepository = jpaTeamRepository;
        this.jpaTeamPlayerRepository = jpaTeamPlayerRepository;
        this.jpaPlayerRepository = jpaPlayerRepository;
    }

    @Override
    public List<Team> findAll() {
        return jpaTeamRepository.findAll().stream()
                .map(TeamMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> findAllActive() {
        return jpaTeamRepository.findByActivoTrue().stream()
                .map(TeamMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Team> findAllActive(Pageable pageable) {
        return jpaTeamRepository.findByActivoTrue(pageable)
                .map(TeamMapper::toDomain);
    }

    @Override
    public Optional<Team> findById(Long id) {
        return jpaTeamRepository.findById(id)
                .map(TeamMapper::toDomain);
    }

    @Override
    public Team save(Team team) {
        TeamEntity entity = TeamMapper.toEntity(team);
        TeamEntity saved = jpaTeamRepository.save(entity);
        return TeamMapper.toDomain(saved);
    }

    @Override
    public Team update(Team team) {
        return save(team);
    }

    @Override
    public void deleteById(Long id) {
        jpaTeamRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaTeamRepository.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaTeamRepository.existsByNombre(nombre);
    }

    @Override
    public void assignPlayerToTeam(Long teamId, Long playerId, String rol) {
        TeamEntity team = jpaTeamRepository.getReferenceById(teamId);
        PlayerEntity player = jpaPlayerRepository.getReferenceById(playerId);
        var existing = jpaTeamPlayerRepository.findByEquipoIdAndJugadorId(teamId, playerId);
        if (existing.isPresent()) {
            TeamPlayerEntity tp = existing.get();
            tp.setRol(rol);
            tp.setActivo(true);
            jpaTeamPlayerRepository.save(tp);
        } else {
            TeamPlayerEntity tp = new TeamPlayerEntity();
            tp.setEquipo(team);
            tp.setJugador(player);
            tp.setRol(rol);
            tp.setActivo(true);
            jpaTeamPlayerRepository.save(tp);
        }
    }

    @Override
    public void removePlayerFromTeam(Long teamId, Long playerId) {
        jpaTeamPlayerRepository.findByEquipoIdAndJugadorId(teamId, playerId)
                .ifPresent(tp -> {
                    tp.setActivo(false);
                    jpaTeamPlayerRepository.save(tp);
                });
    }

    @Override
    public void removeAllPlayersFromTeam(Long teamId) {
        List<TeamPlayerEntity> active = jpaTeamPlayerRepository.findByEquipoIdAndActivoTrue(teamId);
        for (TeamPlayerEntity tp : active) {
            tp.setActivo(false);
            jpaTeamPlayerRepository.save(tp);
        }
    }

    @Override
    public Map<Long, String> findPlayerRolesByTeamId(Long teamId) {
        return jpaTeamPlayerRepository.findByEquipoIdAndActivoTrue(teamId).stream()
                .collect(Collectors.toMap(
                        tp -> tp.getJugador().getId(),
                        tp -> tp.getRol() != null ? tp.getRol() : ""
                ));
    }

    @Override
    public int countActivePlayersByTeamId(Long teamId) {
        return jpaTeamPlayerRepository.countByEquipoIdAndActivoTrue(teamId);
    }

    @Override
    public String findTeamNameByPlayerId(Long playerId) {
        return jpaTeamPlayerRepository.findTeamNameByJugadorId(playerId);
    }

    @Override
    public String findPlayerRoleByPlayerId(Long playerId) {
        return jpaTeamPlayerRepository.findRolByJugadorId(playerId);
    }

    @Override
    public Map<Long, Integer> countActivePlayersByTeamIds(Set<Long> teamIds) {
        if (teamIds.isEmpty()) return Collections.emptyMap();
        return jpaTeamPlayerRepository.countByEquipoIdsAndActivoTrue(teamIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    @Override
    public Map<Long, String> findTeamNamesByPlayerIds(Set<Long> playerIds) {
        if (playerIds.isEmpty()) return Collections.emptyMap();
        return jpaTeamPlayerRepository.findTeamNamesByJugadorIds(playerIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1]
                ));
    }

    @Override
    public Map<Long, String> findRolesByPlayerIds(Set<Long> playerIds) {
        if (playerIds.isEmpty()) return Collections.emptyMap();
        return jpaTeamPlayerRepository.findRolesByJugadorIds(playerIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1]
                ));
    }
}
