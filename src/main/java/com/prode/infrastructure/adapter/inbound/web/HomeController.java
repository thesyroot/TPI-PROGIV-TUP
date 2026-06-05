package com.prode.infrastructure.adapter.inbound.web;

import com.prode.application.service.MatchService;
import com.prode.application.service.PlayerService;
import com.prode.application.service.RoundService;
import com.prode.application.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final TeamService teamService;
    private final PlayerService playerService;
    private final RoundService roundService;
    private final MatchService matchService;

    public HomeController(TeamService teamService, PlayerService playerService,
                          RoundService roundService, MatchService matchService) {
        this.teamService = teamService;
        this.playerService = playerService;
        this.roundService = roundService;
        this.matchService = matchService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalTeams", teamService.findAll().size());
        model.addAttribute("totalPlayers", playerService.findAll().size());
        model.addAttribute("totalRounds", roundService.findAll(null).size());
        model.addAttribute("totalMatches", matchService.findAll(null).size());
        return "index";
    }
}
