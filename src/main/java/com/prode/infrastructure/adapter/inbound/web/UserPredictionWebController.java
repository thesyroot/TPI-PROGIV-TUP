package com.prode.infrastructure.adapter.inbound.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prode.application.dto.request.PredictionRequest;
import com.prode.application.dto.response.MatchResponse;
import com.prode.application.service.MatchService;
import com.prode.application.service.PredictionService;
import com.prode.application.service.RoundService;
import com.prode.application.service.TeamService;

@Controller
@RequestMapping("/user")
public class UserPredictionWebController {

    private final PredictionService predictionService;
    private final MatchService matchService;
    private final RoundService roundService;
    private final TeamService teamService;

    public UserPredictionWebController(PredictionService predictionService,
            MatchService matchService,
            RoundService roundService,
            TeamService teamService) {
        this.predictionService = predictionService;
        this.matchService = matchService;
        this.roundService = roundService;
        this.teamService = teamService;
    }

    // ... otros imports ...

    @GetMapping("/pronosticos")
    public String listPredictions(
            @RequestParam(required = false) Long matchId,
            @RequestParam(required = false) Long jornada,
            @RequestParam(required = false, defaultValue = "false") boolean soloMios,
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        String currentEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long usuarioId = soloMios ? predictionService.findUserIdByEmail(currentEmail) : null;

        Page<com.prode.application.dto.response.PredictionResponse> predictionsPage = predictionService
                .findAllFilteredSecure(matchId, jornada, usuarioId, currentEmail, isAdmin, pageable);

        // filtro de matches, debn estar en POR_JUGARSE o a más de 30 minutos de iniciar
        List<MatchResponse> upcomingMatches = matchService.findAll((Long) null).stream()
                .filter(m -> "POR_JUGARSE".equals(m.getEstado()) &&
                        m.getFecha().minusMinutes(30).isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        Long currentUserId = predictionService.findUserIdByEmail(currentEmail);

        model.addAttribute("predictionsPage", predictionsPage);
        model.addAttribute("upcomingMatches", upcomingMatches);

        // CORRECCIÓN AQUÍ: Casteamos a (Long) y (String) para evitar ambigüedades
        model.addAttribute("matches", matchService.findAll((Long) null));
        model.addAttribute("rounds", roundService.findAll((String) null));

        model.addAttribute("selectedMatchId", matchId);
        model.addAttribute("selectedJornada", jornada);
        model.addAttribute("soloMios", soloMios);
        model.addAttribute("pageTitle", "Pronosticos");

        model.addAttribute("currentUserId", currentUserId);
        return "user/predictions/list";
    }

    @GetMapping("/pronosticos/nuevo")
    public String newPredictionForm(@RequestParam(required = false) Long matchId, Model model) {
        model.addAttribute("predictionRequest", new PredictionRequest());
        model.addAttribute("matchId", matchId);
        if (matchId != null) {
            try {
                model.addAttribute("match", matchService.findById(matchId));
            } catch (Exception e) {
                model.addAttribute("match", null);
            }
        }
        model.addAttribute("pageTitle", "Nuevo Pronostico");
        return "user/predictions/form";
    }

    @PostMapping("/pronosticos")
    public String createPrediction(@ModelAttribute PredictionRequest request,
            Authentication authentication,
            RedirectAttributes redirect) {
        try {
            predictionService.create(request, authentication.getName());
            redirect.addFlashAttribute("success", "Pronostico guardado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/pronosticos";
    }

    @GetMapping("/pronosticos/{id}/editar")
    public String editPredictionForm(@PathVariable Long id, Authentication authentication, Model model,
            RedirectAttributes redirect) {
        try {
            var prediction = predictionService.findById(id);

            // Obtenemos el ID del usuario logueado
            Long currentUserId = predictionService.findUserIdByEmail(authentication.getName());

            // Si la predicción no pertenece al usuario actual, redirigimos con un error
            if (!prediction.getUserId().equals(currentUserId)) {
                return "redirect:/user/pronosticos";
            }

            if (prediction.getFechaPartido().minusMinutes(30).isBefore(LocalDateTime.now())) {
                redirect.addFlashAttribute("error", "Ya no puedes editar este pronóstico.");
                return "redirect:/user/pronosticos";
            }

            PredictionRequest request = new PredictionRequest();
            request.setMatchId(prediction.getMatchId());
            request.setPuntosLocal(prediction.getPuntosLocal());
            request.setPuntosVisitante(prediction.getPuntosVisitante());

            model.addAttribute("predictionRequest", request);
            model.addAttribute("predictionId", id);
            model.addAttribute("match", matchService.findById(prediction.getMatchId()));
            model.addAttribute("pageTitle", "Editar Pronostico");
            return "user/predictions/form";
        } catch (Exception e) {
            return "redirect:/user/pronosticos";
        }
    }

    @GetMapping("/equipo/{id}")
    public String teamDetail(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.findById(id));
        model.addAttribute("pageTitle", "Detalle de Equipo");
        return "user/teams/detail"; 
    }

    @PostMapping("/pronosticos/{id}/actualizar")
    public String updatePrediction(@PathVariable Long id,
            @ModelAttribute PredictionRequest request,
            Authentication authentication,
            RedirectAttributes redirect) {
        try {
            predictionService.update(id, request, authentication.getName());
            redirect.addFlashAttribute("success", "Pronostico actualizado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/pronosticos";
    }

    @PostMapping("/pronosticos/{id}/cancelar")
    public String deletePrediction(@PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirect) {
        try {
            predictionService.delete(id, authentication.getName());
            redirect.addFlashAttribute("success", "Pronostico cancelado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/pronosticos";
    }
}