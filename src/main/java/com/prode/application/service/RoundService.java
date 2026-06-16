package com.prode.application.service;

import com.prode.application.dto.request.RoundRequest;
import com.prode.application.dto.response.RoundResponse;
import com.prode.domain.enums.EstadoJornada;
import com.prode.domain.model.Round;
import com.prode.domain.port.outbound.MatchRepository;
import com.prode.domain.port.outbound.RoundRepository;
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
public class RoundService {

    private final RoundRepository roundRepository;
    private final MatchRepository matchRepository;

    public RoundService(RoundRepository roundRepository, MatchRepository matchRepository) {
        this.roundRepository = roundRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<RoundResponse> findAll(String estado) {
        List<Round> rounds;
        if (estado != null && !estado.isEmpty()) {
            try {
                EstadoJornada estadoEnum = EstadoJornada.valueOf(estado.toUpperCase());
                rounds = roundRepository.findByEstado(estadoEnum);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Estado de jornada invalido: " + estado);
            }
        } else {
            rounds = roundRepository.findAll();
        }
        return toResponseList(rounds);
    }

    @Transactional(readOnly = true)
    public Page<RoundResponse> findAll(String estado, Pageable pageable) {
        Page<Round> page;
        if (estado != null && !estado.isEmpty()) {
            try {
                EstadoJornada estadoEnum = EstadoJornada.valueOf(estado.toUpperCase());
                page = roundRepository.findByEstado(estadoEnum, pageable);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Estado de jornada invalido: " + estado);
            }
        } else {
            page = roundRepository.findAll(pageable);
        }
        List<RoundResponse> responses = toResponseList(page.getContent());
        return new org.springframework.data.domain.PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public RoundResponse findById(Long id) {
        Round round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con id: " + id));
        return toResponse(round);
    }

    public RoundResponse create(RoundRequest request) {
        if (roundRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una jornada con el nombre: " + request.getNombre());
        }
        Round round = new Round();
        round.setNombre(request.getNombre());
        round.setInicioJornada(request.getInicioJornada());
        round.setFinJornada(request.getFinJornada());
        round.setEstado(EstadoJornada.PROGRAMADA);
        Round saved = roundRepository.save(round);
        return toResponse(saved);
    }

    public RoundResponse update(Long id, RoundRequest request) {
        Round round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con id: " + id));
        if (!round.getNombre().equals(request.getNombre()) && roundRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una jornada con el nombre: " + request.getNombre());
        }
        round.setNombre(request.getNombre());
        round.setInicioJornada(request.getInicioJornada());
        round.setFinJornada(request.getFinJornada());
        Round updated = roundRepository.update(round);
        return toResponse(updated);
    }

    public void delete(Long id) {
        Round round = roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada no encontrada con id: " + id));
        if (round.getEstado() != EstadoJornada.PROGRAMADA) {
            throw new BusinessException("Solo se pueden eliminar jornadas en estado PROGRAMADA");
        }
        if (roundRepository.countMatchesByRoundId(id) > 0) {
            throw new BusinessException("No se puede eliminar la jornada porque tiene partidos asociados");
        }
        roundRepository.deleteById(id);
    }

    private List<RoundResponse> toResponseList(List<Round> rounds) {
        if (rounds.isEmpty()) return Collections.emptyList();
        Set<Long> roundIds = rounds.stream().map(Round::getId).collect(Collectors.toSet());
        Map<Long, Long> counts = roundRepository.countMatchesByRoundIds(roundIds);
        return rounds.stream().map(r -> {
            RoundResponse response = new RoundResponse();
            response.setId(r.getId());
            response.setNombre(r.getNombre());
            response.setInicioJornada(r.getInicioJornada());
            response.setFinJornada(r.getFinJornada());
            response.setEstado(r.getEstado() != null ? r.getEstado().name() : "PROGRAMADA");
            response.setCantidadPartidos(counts.getOrDefault(r.getId(), 0L).intValue());
            return response;
        }).collect(Collectors.toList());
    }

    private RoundResponse toResponse(Round round) {
        RoundResponse response = new RoundResponse();
        response.setId(round.getId());
        response.setNombre(round.getNombre());
        response.setInicioJornada(round.getInicioJornada());
        response.setFinJornada(round.getFinJornada());
        response.setEstado(round.getEstado() != null ? round.getEstado().name() : "PROGRAMADA");
        response.setCantidadPartidos((int) roundRepository.countMatchesByRoundId(round.getId()));
        return response;
    }
}
