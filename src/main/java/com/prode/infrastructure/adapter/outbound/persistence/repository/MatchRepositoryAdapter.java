package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.domain.model.Match;
import com.prode.domain.enums.EstadoPartido;
import com.prode.domain.port.outbound.MatchRepository;
import com.prode.infrastructure.adapter.outbound.persistence.entity.MatchEntity;
import com.prode.infrastructure.adapter.outbound.persistence.entity.RoundEntity;
import com.prode.infrastructure.adapter.outbound.persistence.entity.TeamEntity;
import com.prode.infrastructure.adapter.outbound.persistence.mapper.MatchMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MatchRepositoryAdapter implements MatchRepository {

    private final JpaMatchRepository jpaMatchRepository;
    private final JpaRoundRepository jpaRoundRepository;
    private final JpaTeamRepository jpaTeamRepository;

    public MatchRepositoryAdapter(JpaMatchRepository jpaMatchRepository,
                                   JpaRoundRepository jpaRoundRepository,
                                   JpaTeamRepository jpaTeamRepository) {
        this.jpaMatchRepository = jpaMatchRepository;
        this.jpaRoundRepository = jpaRoundRepository;
        this.jpaTeamRepository = jpaTeamRepository;
    }

    @Override
    public List<Match> findAll() {
        return jpaMatchRepository.findAll().stream()
                .map(MatchMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Match> findById(Long id) {
        return jpaMatchRepository.findById(id)
                .map(MatchMapper::toDomain);
    }

    @Override
    public List<Match> findByRoundId(Long roundId) {
        return jpaMatchRepository.findByJornadaIdWithRelations(roundId).stream()
                .map(MatchMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Match> findByRoundId(Long roundId, Pageable pageable) {
        return jpaMatchRepository.findByJornadaIdWithRelations(roundId, pageable)
                .map(MatchMapper::toDomain);
    }

    @Override
    public List<Match> findByEstado(EstadoPartido estado) {
        return jpaMatchRepository.findByEstado(estado.name()).stream()
                .map(MatchMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Match> findAllOrderByFechaAsc() {
        return jpaMatchRepository.findAllWithRelationsByOrderByFechaAsc().stream()
                .map(MatchMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Match> findAllOrderByFechaAsc(Pageable pageable) {
        return jpaMatchRepository.findAllWithRelationsByOrderByFechaAsc(pageable)
                .map(MatchMapper::toDomain);
    }

    @Override
    public Match save(Match match) {
        MatchEntity entity = MatchMapper.toEntity(match);
        if (match.getRound() != null && match.getRound().getId() != null) {
            entity.setJornada(jpaRoundRepository.getReferenceById(match.getRound().getId()));
        }
        if (match.getEquipoLocal() != null && match.getEquipoLocal().getId() != null) {
            entity.setEquipoLocal(jpaTeamRepository.getReferenceById(match.getEquipoLocal().getId()));
        }
        if (match.getEquipoVisitante() != null && match.getEquipoVisitante().getId() != null) {
            entity.setEquipoVisitante(jpaTeamRepository.getReferenceById(match.getEquipoVisitante().getId()));
        }
        MatchEntity saved = jpaMatchRepository.save(entity);
        return MatchMapper.toDomain(saved);
    }

    @Override
    public Match update(Match match) {
        MatchEntity entity = jpaMatchRepository.findById(match.getId())
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        entity.setFecha(match.getFecha());
        entity.setEstado(match.getEstado() != null ? match.getEstado().name() : entity.getEstado());
        entity.setPuntosLocal(match.getPuntosLocal());
        entity.setPuntosVisitante(match.getPuntosVisitante());
        entity.setResultado(match.getResultado());
        if (match.getRound() != null && match.getRound().getId() != null) {
            entity.setJornada(jpaRoundRepository.getReferenceById(match.getRound().getId()));
        }
        if (match.getEquipoLocal() != null && match.getEquipoLocal().getId() != null) {
            entity.setEquipoLocal(jpaTeamRepository.getReferenceById(match.getEquipoLocal().getId()));
        }
        if (match.getEquipoVisitante() != null && match.getEquipoVisitante().getId() != null) {
            entity.setEquipoVisitante(jpaTeamRepository.getReferenceById(match.getEquipoVisitante().getId()));
        }
        MatchEntity saved = jpaMatchRepository.save(entity);
        return MatchMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaMatchRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaMatchRepository.existsById(id);
    }

    @Override
    public long countPredictionsByMatchId(Long matchId) {
        return jpaMatchRepository.countPredictionsByMatchId(matchId);
    }

    @Override
    public boolean existsMatchWithTeam(Long teamId) {
        return jpaMatchRepository.existsMatchWithTeam(teamId);
    }
}
