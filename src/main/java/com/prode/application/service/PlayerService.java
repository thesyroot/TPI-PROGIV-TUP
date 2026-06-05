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
import java.util.List;
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
        return playerRepository.findUnassigned().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findUnassignedOrByTeamId(Long teamId) {
        return playerRepository.findUnassignedOrByTeamId(teamId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findAll() {
        return playerRepository.findAllActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PlayerResponse> findAll(Pageable pageable) {
        return playerRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> searchByNombre(String nombre) {
        return playerRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
        return playerRepository.findByTeamId(teamId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
