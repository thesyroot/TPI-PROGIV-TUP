package com.prode.infrastructure.adapter.inbound.web;

import com.prode.application.service.MatchService;
import com.prode.application.service.RoundService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final RoundService roundService;
    private final MatchService matchService;

    public UserDashboardController(RoundService roundService, MatchService matchService) {
        this.roundService = roundService;
        this.matchService = matchService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalRounds", roundService.findAll(null).size());
        model.addAttribute("totalMatches", matchService.findAll(null).size());
        model.addAttribute("proximosPartidos", matchService.findAll(null).stream()
                .filter(m -> "POR_JUGARSE".equals(m.getEstado()))
                .limit(5)
                .toList());
        model.addAttribute("pageTitle", "Dashboard");
        return "user/dashboard";
    }

    @GetMapping("/ranking")
    public String ranking(Model model) {
        model.addAttribute("pageTitle", "Ranking");
        return "user/ranking";
    }
}