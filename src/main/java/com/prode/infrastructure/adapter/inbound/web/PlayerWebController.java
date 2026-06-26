package com.prode.infrastructure.adapter.inbound.web;

import com.prode.application.dto.request.PlayerRequest;
import com.prode.application.service.PlayerService;
import com.prode.application.service.TeamService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/players")
public class PlayerWebController {

    private final PlayerService playerService;
    private final TeamService teamService;

    public PlayerWebController(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @GetMapping
    public String list(Model model,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        model.addAttribute("pageData", playerService.findAll(pageable));
        return "players/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("playerRequest", new PlayerRequest());
        return "players/form";
    }

    @PostMapping
    public String create(@ModelAttribute PlayerRequest request, RedirectAttributes redirect) {
        try {
            playerService.create(request);
            redirect.addFlashAttribute("success", "Jugador creado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/players";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var player = playerService.findById(id);
        PlayerRequest request = new PlayerRequest();
        request.setNombre(player.getNombre());
        request.setApellido(player.getApellido());
        request.setNumeroCamiseta(player.getNumeroCamiseta());
        request.setImagenUrl(player.getImagenUrl());
        
        model.addAttribute("playerRequest", request);
        model.addAttribute("playerId", id);
        return "players/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute PlayerRequest request, RedirectAttributes redirect) {
        try {
            playerService.update(id, request);
            redirect.addFlashAttribute("success", "Jugador actualizado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/players";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            playerService.delete(id);
            redirect.addFlashAttribute("success", "Jugador eliminado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/players";
    }
}
