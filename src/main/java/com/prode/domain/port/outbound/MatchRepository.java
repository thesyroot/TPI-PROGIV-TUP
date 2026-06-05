package com.prode.domain.port.outbound;

import com.prode.domain.model.Match;
import com.prode.domain.enums.EstadoPartido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

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
}
