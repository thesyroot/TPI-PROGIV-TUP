package com.prode.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prode.application.dto.request.PredictionRequest;
import com.prode.application.dto.response.PredictionResponse;
import com.prode.domain.enums.EstadoPartido;
import com.prode.domain.enums.EstadoPrediccion;
import com.prode.domain.enums.Tendencia;
import com.prode.domain.model.Match;
import com.prode.domain.model.Prediction;
import com.prode.domain.model.User;
import com.prode.domain.port.outbound.MatchRepository;
import com.prode.domain.port.outbound.PredictionRepository;
import com.prode.domain.port.outbound.UserRepository;
import com.prode.shared.exception.BusinessException;
import com.prode.shared.exception.ResourceNotFoundException;

@Service
@Transactional
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public PredictionService(PredictionRepository predictionRepository,
            MatchRepository matchRepository,
            UserRepository userRepository) {
        this.predictionRepository = predictionRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Long findUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
                .getId();
    }

    @Transactional(readOnly = true)
    public List<PredictionResponse> findByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        List<Prediction> predictions = predictionRepository.findByUsuarioId(user.getId());
        return predictions.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PredictionResponse> findByUserId(Long userId) {
        List<Prediction> predictions = predictionRepository.findByUsuarioId(userId);
        return predictions.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PredictionResponse> findByMatchId(Long matchId) {
        List<Prediction> predictions = predictionRepository.findByPartidoId(matchId);
        return predictions.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PredictionResponse> findAll() {
        return predictionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PredictionResponse> findAllFilteredSecure(Long matchId, Long jornadaId, Long usuarioId,
            String currentEmail, boolean isAdmin, Pageable pageable) {

        // Obtenemos todos los pronósticos de la base de datos según los filtros de
        // búsqueda
        Page<Prediction> predictions = predictionRepository.findAllFiltered(matchId, jornadaId, usuarioId, pageable);

        return predictions.map(p -> {
            PredictionResponse res = toResponse(p);

            // Evaluamos las condiciones de privacidad
            boolean isOwner = p.getUser().getEmail().equals(currentEmail);
            boolean isLocked = p.getMatch().getFecha().minusMinutes(30).isBefore(LocalDateTime.now());

            // Si NO es el dueño, NO es admin, y el partido NO está bloqueado -> Ocultamos
            // los números
            if (!isOwner && !isAdmin && !isLocked) {
                res.setPuntosLocal(null);
                res.setPuntosVisitante(null);
                // Opcional: También puedes ocultar la tendencia si quieres total privacidad
                res.setTendencia(null);
            }

            return res;
        });
    }

    @Transactional(readOnly = true)
    public PredictionResponse findById(Long id) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pronostico no encontrado con id: " + id));
        return toResponse(prediction);
    }

    public PredictionResponse create(PredictionRequest request, String userEmail) {
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Partido no encontrado con id: " + request.getMatchId()));

        validateMatchForPrediction(match);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Optional<Prediction> existing = predictionRepository.findByUsuarioIdAndPartidoId(user.getId(), match.getId());
        if (existing.isPresent()) {
            Prediction prediction = existing.get();
            validatePredictionNotLocked(match, prediction);
            prediction.setPuntosLocal(request.getPuntosLocal());
            prediction.setPuntosVisitante(request.getPuntosVisitante());
            prediction.setTendencia(calculateTendencia(request.getPuntosLocal(), request.getPuntosVisitante()));
            prediction.setFechaModificacion(LocalDateTime.now());
            Prediction saved = predictionRepository.save(prediction);
            return toResponse(saved);
        }

        Prediction prediction = new Prediction();
        prediction.setMatch(match);
        prediction.setUser(user);
        prediction.setPuntosLocal(request.getPuntosLocal());
        prediction.setPuntosVisitante(request.getPuntosVisitante());
        prediction.setEstado(EstadoPrediccion.ACTIVO);
        prediction.setTendencia(calculateTendencia(request.getPuntosLocal(), request.getPuntosVisitante()));
        prediction.setFechaCarga(LocalDateTime.now());
        prediction.setFechaModificacion(LocalDateTime.now());

        Prediction saved = predictionRepository.save(prediction);
        return toResponse(saved);
    }

    public PredictionResponse update(Long id, PredictionRequest request, String userEmail) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pronostico no encontrado con id: " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!prediction.getUser().getId().equals(user.getId())) {
            throw new BusinessException("No puedes modificar un pronostico de otro usuario");
        }

        Match match = prediction.getMatch();
        if (!match.getId().equals(request.getMatchId())) {
            throw new BusinessException("No se puede cambiar el partido de un pronostico existente");
        }

        validatePredictionNotLocked(match, prediction);

        prediction.setPuntosLocal(request.getPuntosLocal());
        prediction.setPuntosVisitante(request.getPuntosVisitante());
        prediction.setTendencia(calculateTendencia(request.getPuntosLocal(), request.getPuntosVisitante()));
        prediction.setFechaModificacion(LocalDateTime.now());

        Prediction saved = predictionRepository.save(prediction);
        return toResponse(saved);
    }

    public void delete(Long id, String userEmail) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pronostico no encontrado con id: " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!prediction.getUser().getId().equals(user.getId())) {
            throw new BusinessException("No puedes eliminar un pronostico de otro usuario");
        }

        prediction.setEstado(EstadoPrediccion.ELIMINADO);
        prediction.setFechaModificacion(LocalDateTime.now());
        predictionRepository.save(prediction);
    }

    private void validateMatchForPrediction(Match match) {
        if (match.getFecha().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El partido ya ha finalizado");
        }
        if (match.getFecha().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede pronosticar un partido que comienza en menos de 30 minutos");
        }
        if (match.getEstado() != EstadoPartido.POR_JUGARSE) {
            throw new BusinessException("El partido no esta en estado POR JUGARSE");
        }
    }

    private void validatePredictionNotLocked(Match match, Prediction prediction) {
        if (prediction.getEstado() == EstadoPrediccion.RESUELTO) {
            throw new BusinessException("No se puede modificar un pronostico ya resuelto");
        }
        if (match.getFecha().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new BusinessException(
                    "No se puede modificar un pronostico cuando faltan menos de 30 minutos para el partido");
        }
    }

    private Tendencia calculateTendencia(Integer local, Integer visitante) {
        if (local > visitante)
            return Tendencia.LOCAL;
        if (local < visitante)
            return Tendencia.VISITANTE;
        return Tendencia.EMPATE;
    }

    public PredictionResponse toResponse(Prediction prediction) {
        PredictionResponse response = new PredictionResponse();
        response.setId(prediction.getId());
        response.setMatchId(prediction.getMatch().getId());
        
        response.setLocalId(prediction.getMatch().getEquipoLocal().getId());
        response.setVisitanteId(prediction.getMatch().getEquipoVisitante().getId());
        
        response.setLocal(prediction.getMatch().getEquipoLocal().getNombre());
        response.setLocalImagen(prediction.getMatch().getEquipoLocal().getImagenUrl());
        response.setVisitante(prediction.getMatch().getEquipoVisitante().getNombre());
        response.setVisitanteImagen(prediction.getMatch().getEquipoVisitante().getImagenUrl());
        response.setFechaPartido(prediction.getMatch().getFecha());
        if (prediction.getMatch().getRound() != null) {
            response.setJornada(prediction.getMatch().getRound().getNombre());
        }
        response.setPuntosLocal(prediction.getPuntosLocal());
        response.setPuntosVisitante(prediction.getPuntosVisitante());
        response.setEstado(prediction.getEstado());
        response.setTendencia(prediction.getTendencia());
        response.setUserId(prediction.getUser().getId());
        response.setUserNombre(prediction.getUser().getNombre() + " " + prediction.getUser().getApellido());
        response.setFechaCarga(prediction.getFechaCarga());
        response.setFechaModificacion(prediction.getFechaModificacion());
        if (prediction.getPoints() != null) {
            response.setPuntosObtenidos(prediction.getPoints().getValor());
        }

        // Validación de edicion y observación de predicción dentro de 30 minutos previos al partido
        boolean isLocked = prediction.getMatch().getFecha().minusMinutes(30).isBefore(LocalDateTime.now());
        response.setBloqueado(isLocked); 

        return response;
    }
}