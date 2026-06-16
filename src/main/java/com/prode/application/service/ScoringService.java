package com.prode.application.service;

import com.prode.domain.enums.EstadoPrediccion;
import com.prode.domain.enums.Tendencia;
import com.prode.domain.model.Match;
import com.prode.domain.model.Points;
import com.prode.domain.model.Prediction;
import com.prode.domain.model.User;
import com.prode.domain.port.outbound.MatchRepository;
import com.prode.domain.port.outbound.PointsRepository;
import com.prode.domain.port.outbound.PredictionRepository;
import com.prode.domain.port.outbound.UserRepository;
import com.prode.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScoringService {

    private static final int PUNTOS_EXACTO    = 3;
    private static final int PUNTOS_TENDENCIA = 1;
    private static final int PUNTOS_NULO      = 0;

    private final PredictionRepository predictionRepository;
    private final PointsRepository     pointsRepository;
    private final UserRepository       userRepository;
    private final MatchRepository      matchRepository;

    public ScoringService(PredictionRepository predictionRepository,
                          PointsRepository pointsRepository,
                          UserRepository userRepository,
                          MatchRepository matchRepository) {
        this.predictionRepository = predictionRepository;
        this.pointsRepository     = pointsRepository;
        this.userRepository       = userRepository;
        this.matchRepository      = matchRepository;
    }

    @Transactional
    public void procesarPuntos(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado: " + matchId));

        Integer realLocal      = match.getPuntosLocal();
        Integer realVisitante  = match.getPuntosVisitante();

        if (realLocal == null || realVisitante == null) {
            throw new IllegalStateException("El partido no tiene resultado registrado.");
        }

        Tendencia tendenciaReal = calcularTendencia(realLocal, realVisitante);

        Points puntosExacto    = obtenerPuntos(PUNTOS_EXACTO);
        Points puntosTendencia = obtenerPuntos(PUNTOS_TENDENCIA);
        Points puntosNulo      = obtenerPuntos(PUNTOS_NULO);

        List<Prediction> predicciones = predictionRepository.findByPartidoIdForScoring(matchId);

        for (Prediction pred : predicciones) {
            Points asignados;

            if (esExacto(pred, realLocal, realVisitante)) {
                asignados = puntosExacto;
            } else if (pred.getTendencia() == tendenciaReal) {
                asignados = puntosTendencia;
            } else {
                asignados = puntosNulo;
            }

            pred.setPoints(asignados);
            pred.setEstado(EstadoPrediccion.RESUELTO);
            pred.setFechaModificacion(LocalDateTime.now());

            User user = userRepository.findById(pred.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            int totalActual = user.getPuntosTotal() != null ? user.getPuntosTotal() : 0;
            user.setPuntosTotal(totalActual + asignados.getValor());
            userRepository.update(user);
        }

        predictionRepository.saveAll(predicciones);
    }

    private boolean esExacto(Prediction pred, Integer realLocal, Integer realVisitante) {
        return pred.getPuntosLocal().equals(realLocal)
            && pred.getPuntosVisitante().equals(realVisitante);
    }

    private Tendencia calcularTendencia(Integer local, Integer visitante) {
        if (local > visitante)  return Tendencia.LOCAL;
        if (local < visitante)  return Tendencia.VISITANTE;
        return Tendencia.EMPATE;
    }


    private Points obtenerPuntos(int valor) {
        return pointsRepository.findByValor(valor).orElseGet(() -> {
            Points p = new Points();
            p.setValor(valor);
            p.setNombre(switch (valor) {
                case 3  -> "Resultado Exacto";
                case 1  -> "Acierto de Tendencia";
                default -> "Sin Acierto";
            });
            p.setActivo(true);
            return pointsRepository.save(p);
        });
    }
}
