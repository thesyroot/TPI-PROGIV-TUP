package com.prode.domain.port.outbound;

import com.prode.domain.model.Round;
import com.prode.domain.enums.EstadoJornada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface RoundRepository {
    List<Round> findAll();
    Page<Round> findAll(Pageable pageable);
    Optional<Round> findById(Long id);
    List<Round> findByEstado(EstadoJornada estado);
    Page<Round> findByEstado(EstadoJornada estado, Pageable pageable);
    Round save(Round round);
    Round update(Round round);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNombre(String nombre);
    long countMatchesByRoundId(Long roundId);
}
