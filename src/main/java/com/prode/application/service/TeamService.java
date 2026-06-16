package com.prode.application.service;

import com.prode.application.dto.request.TeamRequest;
import com.prode.application.dto.response.PlayerResponse;
import com.prode.application.dto.response.TeamResponse;
import com.prode.domain.model.Team;
import com.prode.domain.port.outbound.PlayerRepository;
import com.prode.domain.port.outbound.TeamRepository;
import com.prode.shared.exception.BusinessException;
import com.prode.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchService matchService;

    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository, MatchService matchService) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchService = matchService;
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
        Map<Long, String> roles = teamRepository.findPlayerRolesByTeamId(id);
        List<PlayerResponse> jugadores = playerRepository.findByTeamId(id).stream()
                .map(p -> {
                    PlayerResponse pr = new PlayerResponse();
                    pr.setId(p.getId());
                    pr.setNombre(p.getNombre());
                    pr.setApellido(p.getApellido());
                    pr.setNumeroCamiseta(p.getNumeroCamiseta());
                    pr.setImagenUrl(p.getImagenUrl());
                    pr.setEquipoId(id);
                    pr.setEquipoNombre(team.getNombre());
                    pr.setRol(roles.get(p.getId()));
                    return pr;
                })
                .collect(Collectors.toList());
        response.setJugadores(jugadores);
        response.setCantidadJugadores(jugadores.size());
        return response;
    }

    public TeamResponse create(TeamRequest request) {
        if (teamRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un equipo con el nombre: " + request.getNombre());
        }
        Team team = new Team();
        team.setNombre(request.getNombre());
        team.setImagenUrl(request.getImagenUrl());
        team.setActivo(true);
        Team saved = teamRepository.save(team);
        syncPlayerAssignments(saved.getId(), request.getRoles());
        return toResponse(saved);
    }

    public TeamResponse update(Long id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + id));
        if (!team.getNombre().equals(request.getNombre()) && teamRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe un equipo con el nombre: " + request.getNombre());
        }
        team.setNombre(request.getNombre());
        team.setImagenUrl(request.getImagenUrl());
        Team updated = teamRepository.update(team);
        syncPlayerAssignments(id, request.getRoles());
        return toResponse(updated);
    }

    private void syncPlayerAssignments(Long teamId, Map<Long, String> roles) {
        Map<Long, String> currentRoles = teamRepository.findPlayerRolesByTeamId(teamId);
        for (Map.Entry<Long, String> entry : roles.entrySet()) {
            Long playerId = entry.getKey();
            String newRol = entry.getValue();
            String currentRol = currentRoles.get(playerId);
            if (newRol != null && !newRol.trim().isEmpty()) {
                teamRepository.assignPlayerToTeam(teamId, playerId, newRol);
            } else if (currentRol != null) {
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
        if (teams.isEmpty()) return Collections.emptyList();
        Set<Long> teamIds = teams.stream().map(Team::getId).collect(Collectors.toSet());
        Map<Long, Integer> counts = teamRepository.countActivePlayersByTeamIds(teamIds);
        return teams.stream().map(t -> {
            TeamResponse r = new TeamResponse();
            r.setId(t.getId());
            r.setNombre(t.getNombre());
            r.setImagenUrl(t.getImagenUrl());
            r.setCantidadJugadores(counts.getOrDefault(t.getId(), 0));
            return r;
        }).collect(Collectors.toList());
    }

    private TeamResponse toResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setNombre(team.getNombre());
        response.setImagenUrl(team.getImagenUrl());
        response.setCantidadJugadores(teamRepository.countActivePlayersByTeamId(team.getId()));
        return response;
    }
}
