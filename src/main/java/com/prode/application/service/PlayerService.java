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

import com.prode.application.dto.request.PlayerRequest;
import com.prode.application.dto.response.PlayerResponse;
import com.prode.domain.model.Player;
import com.prode.domain.model.Team;
import com.prode.domain.port.outbound.PlayerRepository;
import com.prode.domain.port.outbound.TeamRepository;
import com.prode.shared.exception.ResourceNotFoundException;

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
        return toResponseList(playerRepository.findByTeamId(teamId));
    }

    public PlayerResponse create(PlayerRequest request) {
        Player player = new Player();
        player.setNombre(request.getNombre());
        player.setApellido(request.getApellido());
        player.setNumeroCamiseta(request.getNumeroCamiseta());
        player.setImagenUrl(request.getImagenUrl());
        player.setActivo(true);

        Player saved = playerRepository.save(player);

        if (request.getEquipoId() != null) {
            Team team = teamRepository.findById(request.getEquipoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Equipo no encontrado con id: " + request.getEquipoId()));
            teamRepository.assignPlayerToTeam(team.getId(), saved.getId(), request.getRol());
        }

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
        if (players.isEmpty())
            return Collections.emptyList();
        Set<Long> playerIds = players.stream().map(Player::getId).collect(Collectors.toSet());

        Map<Long, List<String>> allTeamNames = teamRepository.findAllTeamNamesByPlayerIds(playerIds);
        Map<Long, List<String>> allRoles = teamRepository.findAllRolesByPlayerIds(playerIds);

        return players.stream().map(p -> {
            PlayerResponse r = new PlayerResponse();
            r.setId(p.getId());
            r.setNombre(p.getNombre());
            r.setApellido(p.getApellido());
            r.setNumeroCamiseta(p.getNumeroCamiseta());
            r.setImagenUrl(p.getImagenUrl());

            List<String> equipos = allTeamNames.getOrDefault(p.getId(), Collections.emptyList());
            List<String> roles = allRoles.getOrDefault(p.getId(), Collections.emptyList());

            r.setEquipos(obtenerEquiposUnicos(equipos));
            r.setPosicionPopular(calcularPosicionPopular(roles));
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

        List<String> teamNames = teamRepository.findAllTeamNamesByPlayerId(player.getId());
        List<String> roles = teamRepository.findAllRolesByPlayerId(player.getId());

        response.setEquipos(obtenerEquiposUnicos(teamNames));
        response.setPosicionPopular(calcularPosicionPopular(roles));
        return response;
    }

    private List<String> obtenerEquiposUnicos(List<String> equipos) {
        if (equipos == null || equipos.isEmpty())
            return Collections.emptyList();
        return equipos.stream().distinct().collect(Collectors.toList());
    }

    private String calcularPosicionPopular(List<String> roles) {
        if (roles == null || roles.isEmpty())
            return null;
        return roles.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}