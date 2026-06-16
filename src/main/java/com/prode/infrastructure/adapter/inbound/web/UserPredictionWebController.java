package com.prode.infrastructure.adapter.inbound.web;

import com.prode.application.dto.request.PredictionRequest;
import com.prode.application.dto.response.MatchResponse;
import com.prode.application.service.MatchService;
import com.prode.application.service.PredictionService;
import com.prode.application.service.RoundService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserPredictionWebController {

    private final PredictionService predictionService;
    private final MatchService matchService;
    private final RoundService roundService;

    public UserPredictionWebController(PredictionService predictionService,
                                        MatchService matchService,
                                        RoundService roundService) {
        this.predictionService = predictionService;
        this.matchService = matchService;
        this.roundService = roundService;
    }

    @GetMapping("/pronosticos")
    public String listPredictions(
            @RequestParam(required = false) Long matchId,
            @RequestParam(required = false) Long jornada,
            @RequestParam(required = false, defaultValue = "false") boolean soloMios,
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        Long usuarioId = soloMios ? predictionService.findUserIdByEmail(authentication.getName()) : null;

        Page<com.prode.application.dto.response.PredictionResponse> predictionsPage =
                predictionService.findAllFiltered(matchId, jornada, usuarioId, pageable);

        // Upcoming matches (for carousel)
        List<MatchResponse> upcomingMatches = matchService.findAll(null).stream()
                .filter(m -> "POR_JUGARSE".equals(m.getEstado()))
                .toList();

        model.addAttribute("predictionsPage", predictionsPage);
        model.addAttribute("upcomingMatches", upcomingMatches);
        model.addAttribute("matches", matchService.findAll(null));
        model.addAttribute("rounds", roundService.findAll(null));
        model.addAttribute("selectedMatchId", matchId);
        model.addAttribute("selectedJornada", jornada);
        model.addAttribute("soloMios", soloMios);
        model.addAttribute("pageTitle", "Pronosticos");
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
    public String editPredictionForm(@PathVariable Long id, Model model) {
        try {
            var prediction = predictionService.findById(id);
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