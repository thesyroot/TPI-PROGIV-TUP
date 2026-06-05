package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.domain.model.Round;
import com.prode.domain.enums.EstadoJornada;
import com.prode.domain.port.outbound.RoundRepository;
import com.prode.infrastructure.adapter.outbound.persistence.entity.RoundEntity;
import com.prode.infrastructure.adapter.outbound.persistence.mapper.RoundMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RoundRepositoryAdapter implements RoundRepository {

    private final JpaRoundRepository jpaRoundRepository;
    private final JpaMatchRepository jpaMatchRepository;

    public RoundRepositoryAdapter(JpaRoundRepository jpaRoundRepository, JpaMatchRepository jpaMatchRepository) {
        this.jpaRoundRepository = jpaRoundRepository;
        this.jpaMatchRepository = jpaMatchRepository;
    }

    @Override
    public List<Round> findAll() {
        return jpaRoundRepository.findAll().stream()
                .map(RoundMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Round> findAll(Pageable pageable) {
        return jpaRoundRepository.findAll(pageable)
                .map(RoundMapper::toDomain);
    }

    @Override
    public Optional<Round> findById(Long id) {
        return jpaRoundRepository.findById(id)
                .map(RoundMapper::toDomain);
    }

    @Override
    public List<Round> findByEstado(EstadoJornada estado) {
        return jpaRoundRepository.findByEstado(estado.name()).stream()
                .map(RoundMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Round> findByEstado(EstadoJornada estado, Pageable pageable) {
        return jpaRoundRepository.findByEstado(estado.name()).stream()
                .map(RoundMapper::toDomain)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            int start = (int) pageable.getOffset();
                            int end = Math.min(start + pageable.getPageSize(), list.size());
                            return new org.springframework.data.domain.PageImpl<>(
                                    list.subList(start, end),
                                    pageable,
                                    list.size()
                            );
                        }
                ));
    }

    @Override
    public Round save(Round round) {
        RoundEntity entity = RoundMapper.toEntity(round);
        RoundEntity saved = jpaRoundRepository.save(entity);
        return RoundMapper.toDomain(saved);
    }

    @Override
    public Round update(Round round) {
        return save(round);
    }

    @Override
    public void deleteById(Long id) {
        jpaRoundRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRoundRepository.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaRoundRepository.existsByNombre(nombre);
    }

    @Override
    public long countMatchesByRoundId(Long roundId) {
        return jpaMatchRepository.countByJornadaId(roundId);
    }
}
