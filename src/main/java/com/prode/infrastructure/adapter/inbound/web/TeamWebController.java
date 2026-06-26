package com.prode.infrastructure.adapter.inbound.web;

import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prode.application.dto.request.TeamRequest;
import com.prode.application.dto.response.PlayerResponse;
import com.prode.application.service.PlayerService;
import com.prode.application.service.RoundService;
import com.prode.application.service.TeamService;

@Controller
@RequestMapping("/teams")
public class TeamWebController {

    private final TeamService teamService;
    private final PlayerService playerService;
    private final RoundService roundService;

    public TeamWebController(TeamService teamService, PlayerService playerService, RoundService roundService) {
        this.teamService = teamService;
        this.playerService = playerService;
        this.roundService = roundService;
    }

    @GetMapping
    public String list(Model model,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        model.addAttribute("pageData", teamService.findAll(pageable));
        return "teams/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.findById(id));
        return "teams/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("teamRequest", new TeamRequest());
        model.addAttribute("allPlayers", playerService.findAll()); 
        model.addAttribute("rounds", roundService.findAll((String) null));
        return "teams/form";
    }

    @PostMapping
    public String create(@ModelAttribute TeamRequest request, RedirectAttributes redirect) {
        try {
            teamService.create(request);
            redirect.addFlashAttribute("success", "Equipo creado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teams";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var team = teamService.findById(id);
        TeamRequest request = new TeamRequest();
        request.setNombre(team.getNombre());
        request.setImagenUrl(team.getImagenUrl());
        request.setRoundId(team.getRoundId());
        
        Map<Long, String> assignedRoles = teamService.getPlayerRolesByTeamId(id);
        
        request.setRoles(assignedRoles);
        model.addAttribute("teamRequest", request);
        model.addAttribute("teamId", id);
        model.addAttribute("allPlayers", playerService.findAll()); 
        model.addAttribute("rounds", roundService.findAll((String) null));
        return "teams/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute TeamRequest request, RedirectAttributes redirect) {
        try {
            teamService.update(id, request);
            redirect.addFlashAttribute("success", "Equipo actualizado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teams";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            teamService.delete(id);
            redirect.addFlashAttribute("success", "Equipo eliminado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teams";
    }
}
