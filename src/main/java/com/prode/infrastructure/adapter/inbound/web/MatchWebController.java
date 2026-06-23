package com.prode.infrastructure.adapter.inbound.web;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import com.prode.application.dto.request.MatchRequest;
import com.prode.application.dto.request.MatchResultRequest;
import com.prode.application.service.MatchService;
import com.prode.application.service.RoundService;
import com.prode.application.service.TeamService;

@Controller
@RequestMapping("/matches")
public class MatchWebController {

    private final MatchService matchService;
    private final RoundService roundService;
    private final TeamService teamService;

    public MatchWebController(MatchService matchService, RoundService roundService, TeamService teamService) {
        this.matchService = matchService;
        this.roundService = roundService;
        this.teamService = teamService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long jornadaId, Model model,
            @PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.ASC) Pageable pageable) {
        model.addAttribute("pageData", matchService.findAll(jornadaId, pageable));
        model.addAttribute("rounds", roundService.findAll(null));
        model.addAttribute("selectedJornada", jornadaId);
        return "matches/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("matchRequest", new MatchRequest());
        model.addAttribute("rounds", roundService.findAll(null));
        model.addAttribute("teams", teamService.findAll());
        return "matches/form";
    }

    @PostMapping
    public String create(@ModelAttribute MatchRequest request, RedirectAttributes redirect) {
        try {
            matchService.create(request);
            redirect.addFlashAttribute("success", "Partido creado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matches";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var match = matchService.findById(id);
        MatchRequest request = new MatchRequest();
        request.setJornadaId(match.getJornadaId());
        request.setFecha(match.getFecha());
        request.setEquipoLocalId(match.getEquipoLocalId());
        request.setEquipoVisitanteId(match.getEquipoVisitanteId());
        model.addAttribute("matchRequest", request);
        model.addAttribute("matchId", id);
        model.addAttribute("rounds", roundService.findAll(null));
        model.addAttribute("teams", teamService.findAll());
        return "matches/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute MatchRequest request, RedirectAttributes redirect) {
        try {
            matchService.update(id, request);
            redirect.addFlashAttribute("success", "Partido actualizado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matches";
    }

    @PostMapping("/{id}/start")
    public String startMatch(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            matchService.startMatch(id);
            redirect.addFlashAttribute("success",
                    "El partido ha comenzado oficialmente. Estado de jornada verificado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matches";
    }

    @GetMapping("/{id}/finalizar")
    public String finalizeForm(@PathVariable Long id, Model model) {
        model.addAttribute("match", matchService.findById(id));
        model.addAttribute("matchResultRequest", new MatchResultRequest());
        return "matches/finalizar";
    }

    @PostMapping("/{id}/finalizar")
    public String finalize(@PathVariable Long id, @ModelAttribute MatchResultRequest request, RedirectAttributes redirect) {
        try {
            matchService.finalize(id, request.getPuntosLocal(), request.getPuntosVisitante());
            redirect.addFlashAttribute("success", "Partido finalizado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matches";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            matchService.delete(id);
            redirect.addFlashAttribute("success", "Partido eliminado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matches";
    }
}