package com.prode.infrastructure.adapter.inbound.web;

import com.prode.application.dto.request.RoundRequest;
import com.prode.application.service.RoundService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rounds")
public class RoundWebController {

    private final RoundService roundService;

    public RoundWebController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping
    public String list(Model model,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        model.addAttribute("pageData", roundService.findAll(null, pageable));
        return "rounds/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("roundRequest", new RoundRequest());
        return "rounds/form";
    }

    @PostMapping
    public String create(@ModelAttribute RoundRequest request, RedirectAttributes redirect) {
        try {
            roundService.create(request);
            redirect.addFlashAttribute("success", "Jornada creada exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rounds";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var round = roundService.findById(id);
        RoundRequest request = new RoundRequest();
        request.setNombre(round.getNombre());
        request.setInicioJornada(round.getInicioJornada());
        request.setFinJornada(round.getFinJornada());
        model.addAttribute("roundRequest", request);
        model.addAttribute("roundId", id);
        return "rounds/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute RoundRequest request, RedirectAttributes redirect) {
        try {
            roundService.update(id, request);
            redirect.addFlashAttribute("success", "Jornada actualizada exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rounds";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            roundService.delete(id);
            redirect.addFlashAttribute("success", "Jornada eliminada exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rounds";
    }
}
