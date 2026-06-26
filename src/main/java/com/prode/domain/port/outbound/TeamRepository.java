package com.prode.domain.port.outbound;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prode.domain.model.Team;

public interface TeamRepository {
    List<Team> findAll();
    List<Team> findAllActive();
    Page<Team> findAllActive(Pageable pageable);
    Optional<Team> findById(Long id);
    Team save(Team team);
    Team update(Team team);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNombreAndRoundId(String nombre, Long roundId);
    void assignPlayerToTeam(Long teamId, Long playerId, String rol);
    void removePlayerFromTeam(Long teamId, Long playerId);
    void removeAllPlayersFromTeam(Long teamId);
    Map<Long, String> findPlayerRolesByTeamId(Long teamId);
    int countActivePlayersByTeamId(Long teamId);
    String findTeamNameByPlayerId(Long playerId);
    String findPlayerRoleByPlayerId(Long playerId);

    Map<Long, Integer> countActivePlayersByTeamIds(Set<Long> teamIds);
    Map<Long, String> findTeamNamesByPlayerIds(Set<Long> playerIds);
    Map<Long, String> findRolesByPlayerIds(Set<Long> playerIds);

    List<String> findAllTeamNamesByPlayerId(Long playerId);
    List<String> findAllRolesByPlayerId(Long playerId);
    Map<Long, List<String>> findAllTeamNamesByPlayerIds(Set<Long> playerIds);
    Map<Long, List<String>> findAllRolesByPlayerIds(Set<Long> playerIds);
}
