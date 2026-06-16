package com.prode.application.service;

import com.prode.application.dto.request.PlayerRequest;
import com.prode.application.dto.response.PlayerResponse;
import com.prode.domain.model.Player;
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
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findUnassigned() {
        return toResponseList(playerRepository.findUnassigned());
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findUnassignedOrByTeamId(Long teamId) {
        return toResponseList(playerRepository.findUnassignedOrByTeamId(teamId));
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findAll() {
        return toResponseList(playerRepository.findAllActive());
    }

    @Transactional(readOnly = true)
    public Page<PlayerResponse> findAll(Pageable pageable) {
        Page<Player> page = playerRepository.findAllActive(pageable);
        List<PlayerResponse> responses = toResponseList(page.getContent());
        return new org.springframework.data.domain.PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> searchByNombre(String nombre) {
        return toResponseList(playerRepository.findByNombreContainingIgnoreCase(nombre));
    }

    @Transactional(readOnly = true)
    public PlayerResponse findById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con id: " + id));
        return toResponse(player);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findByTeamId(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Equipo no encontrado con id: " + teamId);
        }
        return toResponseListWithTeam(playerRepository.findByTeamId(teamId), teamId);
    }

    public PlayerResponse create(PlayerRequest request) {
        Player player = new Player();
        player.setNombre(request.getNombre());
        player.setApellido(request.getApellido());
        player.setNumeroCamiseta(request.getNumeroCamiseta());
        player.setImagenUrl(request.getImagenUrl());
        player.setActivo(true);

        if (request.getEquipoId() != null) {
            Team team = teamRepository.findById(request.getEquipoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + request.getEquipoId()));
            player = playerRepository.save(player);
            teamRepository.findById(request.getEquipoId());
            return toResponseWithTeam(player, team, request.getRol());
        }

        Player saved = playerRepository.save(player);
        return toResponse(saved);
    }

    public PlayerResponse update(Long id, PlayerRequest request) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con id: " + id));
        player.setNombre(request.getNombre());
        player.setApellido(request.getApellido());
        player.setNumeroCamiseta(request.getNumeroCamiseta());
        player.setImagenUrl(request.getImagenUrl());
        Player updated = playerRepository.update(player);
        return toResponse(updated);
    }

    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Jugador no encontrado con id: " + id);
        }
        Player player = playerRepository.findById(id).get();
        player.setActivo(false);
        playerRepository.update(player);
    }

    private List<PlayerResponse> toResponseList(List<Player> players) {
        if (players.isEmpty()) return Collections.emptyList();
        Set<Long> playerIds = players.stream().map(Player::getId).collect(Collectors.toSet());
        Map<Long, String> teamNames = teamRepository.findTeamNamesByPlayerIds(playerIds);
        Map<Long, String> roles = teamRepository.findRolesByPlayerIds(playerIds);
        return players.stream().map(p -> {
            PlayerResponse r = new PlayerResponse();
            r.setId(p.getId());
            r.setNombre(p.getNombre());
            r.setApellido(p.getApellido());
            r.setNumeroCamiseta(p.getNumeroCamiseta());
            r.setImagenUrl(p.getImagenUrl());
            r.setEquipoNombre(teamNames.get(p.getId()));
            r.setRol(roles.get(p.getId()));
            return r;
        }).collect(Collectors.toList());
    }

    private List<PlayerResponse> toResponseListWithTeam(List<Player> players, Long teamId) {
        if (players.isEmpty()) return Collections.emptyList();
        Set<Long> playerIds = players.stream().map(Player::getId).collect(Collectors.toSet());
        Map<Long, String> roles = teamRepository.findRolesByPlayerIds(playerIds);
        String teamName = teamRepository.findTeamNameByPlayerId(players.get(0).getId());
        return players.stream().map(p -> {
            PlayerResponse r = new PlayerResponse();
            r.setId(p.getId());
            r.setNombre(p.getNombre());
            r.setApellido(p.getApellido());
            r.setNumeroCamiseta(p.getNumeroCamiseta());
            r.setImagenUrl(p.getImagenUrl());
            r.setEquipoId(teamId);
            r.setEquipoNombre(teamName);
            r.setRol(roles.get(p.getId()));
            return r;
        }).collect(Collectors.toList());
    }

    private PlayerResponse toResponse(Player player) {
        PlayerResponse response = new PlayerResponse();
        response.setId(player.getId());
        response.setNombre(player.getNombre());
        response.setApellido(player.getApellido());
        response.setNumeroCamiseta(player.getNumeroCamiseta());
        response.setImagenUrl(player.getImagenUrl());
        response.setEquipoNombre(teamRepository.findTeamNameByPlayerId(player.getId()));
        response.setRol(teamRepository.findPlayerRoleByPlayerId(player.getId()));
        return response;
    }

    private PlayerResponse toResponseWithTeam(Player player, Team team, String rol) {
        PlayerResponse response = toResponse(player);
        response.setEquipoId(team.getId());
        response.setEquipoNombre(team.getNombre());
        response.setRol(rol != null ? rol : null);
        return response;
    }
}
