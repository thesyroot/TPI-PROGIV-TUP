package com.prode.infrastructure.adapter.inbound.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prode.application.service.MatchService;
import com.prode.application.service.RankingService;
import com.prode.application.service.RoundService;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final RoundService   roundService;
    private final MatchService   matchService;
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
        model.addAttribute("totalMatches", matchService.countFutureMatches());
        model.addAttribute("proximosPartidos", matchService.findAll(null).stream()
                .filter(m -> "POR_JUGARSE".equals(m.getEstado()))
                .limit(5).toList());
        model.addAttribute("pageTitle", "Dashboard");
        return "user/dashboard";
    }

    @GetMapping("/ranking/buscar")
public String buscarUsuarios(
        @RequestParam(required = false, defaultValue = "") String nombre,
        @RequestParam(required = false) List<Long> userIds,
        Model model) {

    List<Long> idsSeleccionados =
            userIds != null ? userIds.stream().distinct().toList() : List.of();

    model.addAttribute("nombre", nombre);

    model.addAttribute(
            "resultados",
            nombre.isBlank()
                    ? List.of()
                    : rankingService.buscarUsuariosPorNombre(nombre)
    );

    model.addAttribute("userIds", idsSeleccionados);

    model.addAttribute(
            "usuariosSeleccionados",
            rankingService.getUsuariosPorIds(idsSeleccionados)
    );

    model.addAttribute("pageTitle", "Buscar jugadores");

    return "user/ranking-buscar";
}
    @GetMapping("/ranking")
    public String ranking(
            @RequestParam(required = false) List<Long> userIds,
            Model model) {

        boolean haySeleccion = userIds != null && !userIds.isEmpty();

        model.addAttribute("ranking", haySeleccion
                ? rankingService.getRankingFiltrado(userIds)
                : rankingService.getRankingGlobal());
        model.addAttribute("usuariosSeleccionados", haySeleccion
                ? rankingService.getUsuariosPorIds(userIds)
                : List.of());
        model.addAttribute("userIds",  userIds != null ? userIds : List.of());
        model.addAttribute("filtrado", haySeleccion);
        model.addAttribute("pageTitle", "Ranking");
        return "user/ranking";
    }
}