package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.domain.model.Prediction;
import com.prode.domain.port.outbound.PredictionRepository;
import com.prode.infrastructure.adapter.outbound.persistence.entity.PredictionEntity;
import com.prode.infrastructure.adapter.outbound.persistence.mapper.PredictionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class PredictionRepositoryAdapter implements PredictionRepository {

    private final JpaPredictionRepository jpaPredictionRepository;
    private final JpaMatchRepository jpaMatchRepository;
    private final JpaUserRepository jpaUserRepository;

    public PredictionRepositoryAdapter(JpaPredictionRepository jpaPredictionRepository,
                                        JpaMatchRepository jpaMatchRepository,
                                        JpaUserRepository jpaUserRepository) {
        this.jpaPredictionRepository = jpaPredictionRepository;
        this.jpaMatchRepository = jpaMatchRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public List<Prediction> findByUsuarioId(Long usuarioId) {
        return jpaPredictionRepository.findByUsuarioIdWithRelations(usuarioId)
                .stream()
                .map(PredictionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Prediction> findByPartidoId(Long partidoId) {
        return jpaPredictionRepository.findByPartidoIdWithRelations(partidoId)
                .stream()
                .map(PredictionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Prediction> findByUsuarioIdAndPartidoId(Long usuarioId, Long partidoId) {
        return jpaPredictionRepository.findByUsuarioIdAndPartidoId(usuarioId, partidoId)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public List<Prediction> findAll() {
        return jpaPredictionRepository.findAllWithRelations()
                .stream()
                .map(PredictionMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Prediction> findAllFiltered(Long matchId, Long jornadaId, Long usuarioId, Pageable pageable) {
        return jpaPredictionRepository.findAllFiltered(matchId, jornadaId, usuarioId, pageable)
                .map(PredictionMapper::toDomain);
    }

    @Override
    public Prediction save(Prediction prediction) {
        PredictionEntity entity = PredictionMapper.toEntity(prediction);
        // ensure managed JPA references
        if (prediction.getMatch() != null && prediction.getMatch().getId() != null) {
            entity.setPartido(jpaMatchRepository.getReferenceById(prediction.getMatch().getId()));
        }
        if (prediction.getUser() != null && prediction.getUser().getId() != null) {
            entity.setUsuario(jpaUserRepository.getReferenceById(prediction.getUser().getId()));
        }
        PredictionEntity saved = jpaPredictionRepository.save(entity);
        return jpaPredictionRepository.findById(saved.getId())
                .map(PredictionMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Optional<Prediction> findById(Long id) {
        return jpaPredictionRepository.findById(id)
                .map(PredictionMapper::toDomain);
    }
}