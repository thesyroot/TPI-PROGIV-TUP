package com.prode.application.service;

import com.prode.application.dto.response.RankingEntryResponse;
import com.prode.domain.model.User;
import com.prode.domain.port.outbound.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class RankingService {

    private final UserRepository userRepository;

    public RankingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** RF7.1 – Ranking global de todos los usuarios activos. */
    public List<RankingEntryResponse> getRankingGlobal() {
        return userRepository.findRankingGlobal();
    }

    /** RF7 extra – Ranking filtrado por un subconjunto de usuarios. */
    public List<RankingEntryResponse> getRankingFiltrado(List<Long> userIds) {
        return userRepository.findRankingByUserIds(userIds);
    }

    /** Devuelve todos los usuarios activos con rol USER para el selector del filtro. */
    public List<User> getUsuariosDisponibles() {
        return userRepository.findAll().stream()
                .filter(u -> u.getActivo() != null && u.getActivo())
                .filter(u -> u.getRol() != null && "USER".equals(u.getRol().name()))
                .toList();
    }
}
