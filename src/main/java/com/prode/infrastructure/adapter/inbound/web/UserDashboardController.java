package com.prode.infrastructure.adapter.inbound.web;


import com.prode.application.service.MatchService;
import com.prode.application.service.RankingService;
import com.prode.application.service.RoundService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final RoundService  roundService;
    private final MatchService  matchService;
    private final RankingService rankingService;

    public UserDashboardController(RoundService roundService,
                                   MatchService matchService,
                                   RankingService rankingService) {
        this.roundService  = roundService;
        this.matchService  = matchService;
        this.rankingService = rankingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalRounds", roundService.findAll(null).size());
        model.addAttribute("totalMatches", matchService.findAll(null).size());
        model.addAttribute("proximosPartidos", matchService.findAll(null).stream()
                .filter(m -> "POR_JUGARSE".equals(m.getEstado()))
                .limit(5).toList());
        model.addAttribute("pageTitle", "Dashboard");
        return "user/dashboard";
    }

    @GetMapping("/ranking")
    public String ranking(@RequestParam(required = false) List<Long> userIds, Model model) {
        boolean filtrado = userIds != null && !userIds.isEmpty();

        model.addAttribute("ranking",
                filtrado ? rankingService.getRankingFiltrado(userIds)
                         : rankingService.getRankingGlobal());

        model.addAttribute("usuarios",   rankingService.getUsuariosDisponibles());
        model.addAttribute("userIds",    userIds != null ? userIds : List.of());
        model.addAttribute("filtrado",   filtrado);
        model.addAttribute("pageTitle",  "Ranking");
        return "user/ranking";
    }
}
