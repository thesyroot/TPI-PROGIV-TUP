package com.prode.infrastructure.adapter.inbound.web;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prode.application.dto.response.MatchResponse;
import com.prode.application.dto.response.PredictionResponse;
import com.prode.application.service.MatchService;
import com.prode.application.service.PredictionService;
import com.prode.application.service.RoundService;

@Controller
@RequestMapping("/user/partidos")
public class UserMatchWebController {

    private final MatchService matchService;
    private final PredictionService predictionService;
    private final RoundService roundService;

    public UserMatchWebController(MatchService matchService,
            PredictionService predictionService,
            RoundService roundService) {
        this.matchService = matchService;
        this.predictionService = predictionService;
        this.roundService = roundService;
    }

    @GetMapping
    public String listFinishedMatches(
            @RequestParam(required = false) Long jornadaId,
            Authentication authentication,
            Model model) {

        String currentEmail = authentication.getName();
        Long userId = predictionService.findUserIdByEmail(currentEmail);

        // 1. Obtenemos solo los partidos FINALIZADOS y los ordenamos por fecha
        // descendente (más recientes primero)
        List<MatchResponse> finishedMatches = matchService.findAll(jornadaId).stream()
                .filter(m -> "FINALIZADO".equals(m.getEstado()))
                .sorted((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()))
                .toList();

        // 2. Obtenemos todos los pronósticos de este usuario
        List<PredictionResponse> userPredictions = predictionService.findByUserId(userId);

        // 3. Cruzamos la información en un DTO para enviarlo limpio a la vista
        List<UserMatchDTO> userMatches = finishedMatches.stream().map(match -> {
            PredictionResponse userPred = userPredictions.stream()
                    .filter(p -> p.getMatchId().equals(match.getId()))
                    .findFirst()
                    .orElse(null);
            return new UserMatchDTO(match, userPred);
        }).toList();

        model.addAttribute("matches", userMatches);
        // Usamos (String) null para evitar la ambigüedad, igual que lo tienes en el
        // UserPredictionWebController
        model.addAttribute("rounds", roundService.findAll((String) null));
        model.addAttribute("selectedJornada", jornadaId);
        model.addAttribute("pageTitle", "Partidos Finalizados");

        return "user/matches/list";
    }

    // DTO interno (Wrapper) para facilitar la lectura en Thymeleaf
    public static class UserMatchDTO {
        private final MatchResponse match;
        private final PredictionResponse prediction;

        public UserMatchDTO(MatchResponse match, PredictionResponse prediction) {
            this.match = match;
            this.prediction = prediction;
        }

        public MatchResponse getMatch() {
            return match;
        }

        public PredictionResponse getPrediction() {
            return prediction;
        }
    }
}