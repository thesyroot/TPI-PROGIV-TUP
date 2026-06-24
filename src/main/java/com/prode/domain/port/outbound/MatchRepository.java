package com.prode.domain.port.outbound;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prode.domain.enums.EstadoPartido;
import com.prode.domain.model.Match;

public interface MatchRepository {
    List<Match> findAll();
    Optional<Match> findById(Long id);
    List<Match> findByRoundId(Long roundId);
    Page<Match> findByRoundId(Long roundId, Pageable pageable);
    List<Match> findByEstado(EstadoPartido estado);
    List<Match> findAllOrderByFechaAsc();
    Page<Match> findAllOrderByFechaAsc(Pageable pageable);
    Match save(Match match);
    Match update(Match match);
    void deleteById(Long id);
    boolean existsById(Long id);
    long countPredictionsByMatchId(Long matchId);
    boolean existsMatchWithTeam(Long teamId);
    // Contador de partidos a futuro
    long countByFechaAfter(LocalDateTime fecha);
}
