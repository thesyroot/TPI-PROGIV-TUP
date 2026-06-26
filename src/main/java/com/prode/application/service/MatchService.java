package com.prode.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prode.application.dto.request.MatchRequest;
import com.prode.application.dto.response.MatchResponse;
import com.prode.domain.enums.EstadoJornada;
import com.prode.domain.enums.EstadoPartido;
import com.prode.domain.model.Match;
import com.prode.domain.model.Round;
import com.prode.domain.model.Team;
import com.prode.domain.port.outbound.MatchRepository;
import com.prode.domain.port.outbound.RoundRepository;
import com.prode.domain.port.outbound.TeamRepository;
import com.prode.shared.exception.BusinessException;
import com.prode.shared.exception.ResourceNotFoundException;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final RoundRepository roundRepository;
    private final TeamRepository teamRepository;
    private final ScoringService scoringService;

    public MatchService(MatchRepository matchRepository,
            RoundRepository roundRepository,
            TeamRepository teamRepository,
            @Lazy ScoringService scoringService) {
        this.matchRepository = matchRepository;
        this.roundRepository = roundRepository;
        this.teamRepository = teamRepository;
        this.scoringService = scoringService;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findAll(Long jornadaId) {
        List<Match> matches = jornadaId != null
                ? matchRepository.findByRoundId(jornadaId)
                : matchRepository.findAllOrderByFechaAsc();
        return matches.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MatchResponse> findAll(Long jornadaId, Pageable pageable) {
        if (jornadaId != null) {
            return matchRepository.findByRoundId(jornadaId, pageable).map(this::toResponse);
        }
        return matchRepository.findAllOrderByFechaAsc(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MatchResponse findById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
        return toResponse(match);
    }

    @Transactional(readOnly = true)
    public long countFutureMatches() {
        // Obtenemos la fecha actual y le sumamos 30 minutos
        LocalDateTime fechaLimite = LocalDateTime.now().plusMinutes(30);
        return matchRepository.countByFechaAfter(fechaLimite);
    }

    public MatchResponse create(MatchRequest request) {
        if (request.getEquipoLocalId().equals(request.getEquipoVisitanteId()))
            throw new BusinessException("El equipo local y visitante deben ser diferentes");

        Round round = roundRepository.findById(request.getJornadaId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Jornada no encontrada con id: " + request.getJornadaId()));
        Team local = teamRepository.findById(request.getEquipoLocalId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipo local no encontrado con id: " + request.getEquipoLocalId()));
        Team visitante = teamRepository.findById(request.getEquipoVisitanteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipo visitante no encontrado con id: " + request.getEquipoVisitanteId()));

        // Regla 1: Ambos equipos deben existir con esa misma jornada
        if (!local.getRoundId().equals(round.getId()) || !visitante.getRoundId().equals(round.getId())) {
            throw new BusinessException("Ambos equipos deben pertenecer a la jornada seleccionada");
        }

        // Regla 3: La jornada no puede tener dos partidos donde juegue el mismo equipo
        List<Match> matchesInRound = matchRepository.findByRoundId(round.getId());
        boolean localYaJuega = matchesInRound.stream()
                .anyMatch(m -> m.getEquipoLocal().getId().equals(local.getId())
                        || m.getEquipoVisitante().getId().equals(local.getId()));
        boolean visitanteYaJuega = matchesInRound.stream()
                .anyMatch(m -> m.getEquipoLocal().getId().equals(visitante.getId())
                        || m.getEquipoVisitante().getId().equals(visitante.getId()));

        if (localYaJuega)
            throw new BusinessException("El equipo local ya tiene un partido asignado en esta jornada");
        if (visitanteYaJuega)
            throw new BusinessException("El equipo visitante ya tiene un partido asignado en esta jornada");

        Match match = new Match();
        match.setRound(round);
        match.setFecha(request.getFecha());
        match.setEquipoLocal(local);
        match.setEquipoVisitante(visitante);
        match.setEstado(EstadoPartido.POR_JUGARSE);

        return toResponse(matchRepository.save(match));
    }

    public MatchResponse update(Long id, MatchRequest request) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));

        if (match.getEstado() != EstadoPartido.POR_JUGARSE)
            throw new BusinessException("Solo se pueden modificar partidos en estado POR JUGARSE");
        if (request.getEquipoLocalId().equals(request.getEquipoVisitanteId()))
            throw new BusinessException("El equipo local y visitante deben ser diferentes");

        Round round = roundRepository.findById(request.getJornadaId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Jornada no encontrada con id: " + request.getJornadaId()));
        Team local = teamRepository.findById(request.getEquipoLocalId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo local no encontrado"));
        Team visitante = teamRepository.findById(request.getEquipoVisitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo visitante no encontrado"));

        
        if (!local.getRoundId().equals(round.getId()) || !visitante.getRoundId().equals(round.getId())) {
            throw new BusinessException("Ambos equipos deben pertenecer a la jornada seleccionada");
        }

        List<Match> matchesInRound = matchRepository.findByRoundId(round.getId());
        // Al actualizar, evitamos evaluar el partido contra sí mismo usando filter
        boolean localYaJuega = matchesInRound.stream()
                .filter(m -> !m.getId().equals(id))
                .anyMatch(m -> m.getEquipoLocal().getId().equals(local.getId())
                        || m.getEquipoVisitante().getId().equals(local.getId()));
        boolean visitanteYaJuega = matchesInRound.stream()
                .filter(m -> !m.getId().equals(id))
                .anyMatch(m -> m.getEquipoLocal().getId().equals(visitante.getId())
                        || m.getEquipoVisitante().getId().equals(visitante.getId()));

        if (localYaJuega)
            throw new BusinessException("El equipo local ya tiene un partido asignado en esta jornada");
        if (visitanteYaJuega)
            throw new BusinessException("El equipo visitante ya tiene un partido asignado en esta jornada");

        match.setRound(round);
        match.setFecha(request.getFecha());
        match.setEquipoLocal(local);
        match.setEquipoVisitante(visitante);

        return toResponse(matchRepository.update(match));
    }

    public void delete(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
        if (match.getEstado() != EstadoPartido.POR_JUGARSE)
            throw new BusinessException("Solo se pueden eliminar partidos en estado POR JUGARSE");
        if (matchRepository.countPredictionsByMatchId(id) > 0)
            throw new BusinessException("No se puede eliminar el partido porque tiene pronosticos registrados");
        matchRepository.deleteById(id);
    }

    // RF4.3: Transición manual a "En juego" de un partido por el administrador.

    public MatchResponse startMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));

        if (match.getEstado() != EstadoPartido.POR_JUGARSE) {
            throw new BusinessException("Solo se pueden iniciar partidos en estado POR_JUGARSE");
        }

        match.setEstado(EstadoPartido.EN_JUEGO);
        Match updated = matchRepository.update(match);

        if (match.getRound() != null) {
            recalculateRoundState(match.getRound().getId());
        }

        return toResponse(updated);
    }

    public MatchResponse finalize(Long id, Integer localScore, Integer visitanteScore) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));

        if (match.getEstado() == EstadoPartido.FINALIZADO)
            throw new BusinessException("El partido ya esta finalizado");

        match.setPuntosLocal(localScore);
        match.setPuntosVisitante(visitanteScore);
        match.setResultado(match.getEquipoLocal().getNombre() + " " + localScore
                + " - " + visitanteScore + " " + match.getEquipoVisitante().getNombre());
        match.setEstado(EstadoPartido.FINALIZADO);

        Match updated = matchRepository.update(match);

        scoringService.procesarPuntos(id);

        if (match.getRound() != null) {
            recalculateRoundState(match.getRound().getId());
        }

        return toResponse(updated);
    }

    // RF3.3: Gestión Automática de Estados de la Fecha.

    private void recalculateRoundState(Long roundId) {
        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con id: " + roundId));

        List<Match> matches = matchRepository.findByRoundId(roundId);

        if (matches.isEmpty()) {
            round.setEstado(EstadoJornada.PROGRAMADA);
            roundRepository.update(round);
            return;
        }

        boolean anyEnJuego = matches.stream()
                .anyMatch(m -> m.getEstado() == EstadoPartido.EN_JUEGO);

        boolean allFinalizados = matches.stream()
                .allMatch(m -> m.getEstado() == EstadoPartido.FINALIZADO);

        boolean allPorJugarse = matches.stream()
                .allMatch(m -> m.getEstado() == EstadoPartido.POR_JUGARSE);

        EstadoJornada nuevoEstado;
        if (anyEnJuego) {
            nuevoEstado = EstadoJornada.EN_JUEGO;
        } else if (allFinalizados) {
            nuevoEstado = EstadoJornada.FINALIZADA;
        } else if (allPorJugarse) {
            nuevoEstado = EstadoJornada.PROGRAMADA;
        } else {
            nuevoEstado = EstadoJornada.EN_JUEGO;
        }

        if (round.getEstado() != nuevoEstado) {
            round.setEstado(nuevoEstado);
            roundRepository.update(round);
        }
    }

    public boolean existsMatchWithTeam(Long teamId) {
        return matchRepository.existsMatchWithTeam(teamId);
    }

    private MatchResponse toResponse(Match match) {
        MatchResponse response = new MatchResponse();
        response.setId(match.getId());
        if (match.getRound() != null) {
            response.setJornadaId(match.getRound().getId());
            response.setJornadaNombre(match.getRound().getNombre());
        }
        response.setFecha(match.getFecha());
        if (match.getEquipoLocal() != null) {
            response.setEquipoLocalId(match.getEquipoLocal().getId());
            response.setEquipoLocalNombre(match.getEquipoLocal().getNombre());
            response.setEquipoLocalImagen(match.getEquipoLocal().getImagenUrl());
        }
        if (match.getEquipoVisitante() != null) {
            response.setEquipoVisitanteId(match.getEquipoVisitante().getId());
            response.setEquipoVisitanteNombre(match.getEquipoVisitante().getNombre());
            response.setEquipoVisitanteImagen(match.getEquipoVisitante().getImagenUrl());
        }
        response.setEstado(match.getEstado() != null ? match.getEstado().name() : "POR_JUGARSE");
        response.setPuntosLocal(match.getPuntosLocal());
        response.setPuntosVisitante(match.getPuntosVisitante());
        response.setResultado(match.getResultado());
        return response;
    }
}