package com.prode.domain.port.outbound;

import com.prode.domain.model.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface PredictionRepository {
    List<Prediction> findByUsuarioId(Long usuarioId);
    List<Prediction> findByPartidoId(Long partidoId);
    Optional<Prediction> findByUsuarioIdAndPartidoId(Long usuarioId, Long partidoId);
    List<Prediction> findAll();
    Page<Prediction> findAllFiltered(Long matchId, Long jornadaId, Long usuarioId, Pageable pageable);
    Prediction save(Prediction prediction);
    Optional<Prediction> findById(Long id);
    List<Prediction> findByPartidoIdForScoring(Long partidoId);
    void saveAll(List<Prediction> predictions);
}
