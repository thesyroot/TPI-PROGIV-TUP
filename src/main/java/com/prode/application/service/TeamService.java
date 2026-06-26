package com.prode.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prode.application.dto.request.TeamRequest;
import com.prode.application.dto.response.PlayerResponse;
import com.prode.application.dto.response.TeamResponse;
import com.prode.domain.model.Player;
import com.prode.domain.model.Round;
import com.prode.domain.model.Team;
import com.prode.domain.port.outbound.PlayerRepository;
import com.prode.domain.port.outbound.RoundRepository;
import com.prode.domain.port.outbound.TeamRepository;
import com.prode.shared.exception.BusinessException;
import com.prode.shared.exception.ResourceNotFoundException;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchService matchService;
    private final RoundRepository roundRepository;

    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository, MatchService matchService,
            RoundRepository roundRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchService = matchService;
        this.roundRepository = roundRepository;
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> findAll() {
        return toResponseList(teamRepository.findAllActive());
    }

    @Transactional(readOnly = true)
    public Page<TeamResponse> findAll(Pageable pageable) {
        Page<Team> page = teamRepository.findAllActive(pageable);
        List<TeamResponse> responses = toResponseList(page.getContent());
        return new org.springframework.data.domain.PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TeamResponse findById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + id));
        TeamResponse response = toResponse(team);
        
        List<Player> jugadoresDelEquipo = playerRepository.findByTeamId(id);
        
        if (jugadoresDelEquipo.isEmpty()) {
            response.setJugadores(Collections.emptyList());
            response.setCantidadJugadores(0);
            return response;
        }

        Set<Long> playerIds = jugadoresDelEquipo.stream().map(Player::getId).collect(Collectors.toSet());
        Map<Long, List<String>> allRoles = teamRepository.findAllRolesByPlayerIds(playerIds);
        Map<Long, List<String>> allTeamNames = teamRepository.findAllTeamNamesByPlayerIds(playerIds);

        List<PlayerResponse> jugadores = jugadoresDelEquipo.stream()
                .map(p -> {
                    PlayerResponse pr = new PlayerResponse();
                    pr.setId(p.getId());
                    pr.setNombre(p.getNombre());
                    pr.setApellido(p.getApellido());
                    pr.setNumeroCamiseta(p.getNumeroCamiseta());
                    pr.setImagenUrl(p.getImagenUrl());
                    
                    List<String> equiposJugador = allTeamNames.getOrDefault(p.getId(), Collections.emptyList());
                    List<String> rolesJugador = allRoles.getOrDefault(p.getId(), Collections.emptyList());
                    
                    pr.setEquipos(equiposJugador.stream().distinct().collect(Collectors.toList()));
                    
                    pr.setPosicionPopular(rolesJugador.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.groupingBy(r -> r, Collectors.counting()))
                            .entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse(null));

                    return pr;
                })
                .collect(Collectors.toList());
                
        response.setJugadores(jugadores);
        response.setCantidadJugadores(jugadores.size());
        return response;
    }

    public TeamResponse create(TeamRequest request) {
        if (teamRepository.existsByNombreAndRoundId(request.getNombre(), request.getRoundId())) {
            throw new BusinessException("Ya existe un equipo con ese nombre en esa jornada");
        }
        Team team = new Team();
        team.setNombre(request.getNombre());
        team.setImagenUrl(request.getImagenUrl());
        team.setRoundId(request.getRoundId());
        team.setActivo(true);
        Team saved = teamRepository.save(team);
        syncPlayerAssignments(saved.getId(), request.getRoles());
        return toResponse(saved);
    }

    public TeamResponse update(Long id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + id));

        boolean nombreCambio = !team.getNombre().equals(request.getNombre());
        boolean jornadaCambio = !Objects.equals(team.getRoundId(), request.getRoundId());

        if ((nombreCambio || jornadaCambio) &&
                teamRepository.existsByNombreAndRoundId(request.getNombre(), request.getRoundId())) {
            throw new BusinessException("Ya existe un equipo con ese nombre en esa jornada");
        }

        team.setNombre(request.getNombre());
        team.setImagenUrl(request.getImagenUrl());
        team.setRoundId(request.getRoundId());
        Team updated = teamRepository.update(team);
        syncPlayerAssignments(id, request.getRoles());
        return toResponse(updated);
    }

    private void syncPlayerAssignments(Long teamId, Map<Long, String> roles) {
        if (roles == null || roles.isEmpty()) {
            teamRepository.removeAllPlayersFromTeam(teamId);
            return;
        }

        // Limitamos la cantidad de jugadores
        if (roles.size() > 26) {
            throw new BusinessException("No se pueden asignar más de 26 jugadores a un equipo a la vez.");
        }

        // validamos los roles permitidos
        Set<String> rolesValidos = Set.of("ARQUERO", "DEFENSOR", "MEDIOCAMPISTA", "DELANTERO");

        Map<Long, String> currentRoles = teamRepository.findPlayerRolesByTeamId(teamId);

        for (Map.Entry<Long, String> entry : roles.entrySet()) {
            Long playerId = entry.getKey();
            String rawRol = entry.getValue();
            
            // Limpieza básica del string
            String newRol = (rawRol != null) ? rawRol.trim().toUpperCase() : "";

            // impedimos ids invalidos
            if (playerId == null || playerId <= 0) continue;

            String currentRol = currentRoles.get(playerId);

            if (!newRol.isEmpty()) {
                if (!rolesValidos.contains(newRol)) {
                    throw new BusinessException("Rol no permitido o inválido: " + newRol);
                }
                
                // buscar si el jugador existe y está activo
                if (!playerRepository.existsById(playerId)) {
                    throw new ResourceNotFoundException("El jugador con ID " + playerId + " no existe.");
                }
                
                teamRepository.assignPlayerToTeam(teamId, playerId, newRol);
            } else if (currentRol != null) {
                // si se envia un rol vacío pero el jugador estaba en el equipo, lo removemos
                teamRepository.removePlayerFromTeam(teamId, playerId);
            }
        }
    }

    public void delete(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + id));
        if (matchService.existsMatchWithTeam(id)) {
            throw new BusinessException("No se puede eliminar el equipo porque esta asociado a partidos");
        }
        team.setActivo(false);
        teamRepository.update(team);
    }

    private List<TeamResponse> toResponseList(List<Team> teams) {
        if (teams.isEmpty())
            return Collections.emptyList();
        Set<Long> teamIds = teams.stream().map(Team::getId).collect(Collectors.toSet());
        Map<Long, Integer> counts = teamRepository.countActivePlayersByTeamIds(teamIds);
        Map<Long, String> roundNames = roundRepository.findAll().stream()
                .collect(Collectors.toMap(Round::getId, Round::getNombre, (b1, b2) -> b1));
        return teams.stream().map(t -> {
            TeamResponse r = new TeamResponse();
            r.setId(t.getId());
            r.setNombre(t.getNombre());
            r.setImagenUrl(t.getImagenUrl());
            r.setRoundId(t.getRoundId());
            r.setCantidadJugadores(counts.getOrDefault(t.getId(), 0));
            // si no es null le asignamos el nombre
            if (t.getRoundId() != null) {
                r.setRoundNombre(roundNames.get(t.getRoundId()));
            }
            return r;
        }).collect(Collectors.toList());
    }

    private TeamResponse toResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setNombre(team.getNombre());
        response.setImagenUrl(team.getImagenUrl());
        response.setRoundId(team.getRoundId());
        // si no es null asignamos el nombre
        if (team.getRoundId() != null) {
            roundRepository.findById(team.getRoundId())
                    .ifPresent(r -> response.setRoundNombre(r.getNombre()));
        }
        response.setCantidadJugadores(teamRepository.countActivePlayersByTeamId(team.getId()));
        return response;
    }

    public Map<Long, String> getPlayerRolesByTeamId(Long teamId) {
        return teamRepository.findPlayerRolesByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public Page<TeamResponse> searchTeams(String nombre, String roundNombre, Pageable pageable) {
        Page<Team> page = teamRepository.searchActiveTeams(nombre, roundNombre, pageable);
        List<TeamResponse> responses = toResponseList(page.getContent());
        return new org.springframework.data.domain.PageImpl<>(responses, pageable, page.getTotalElements());
    }
}
