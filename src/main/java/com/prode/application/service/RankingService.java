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

    public List<RankingEntryResponse> getRankingGlobal() {
        return userRepository.findRankingGlobal();
    }

    public List<RankingEntryResponse> getRankingFiltrado(List<Long> userIds) {
        return userRepository.findRankingByUserIds(userIds);
    }

    public List<User> getUsuariosPorIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        return usuariosActivos().stream()
                .filter(u -> userIds.contains(u.getId()))
                .toList();
    }

    public List<User> buscarUsuariosPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return List.of();
        String query = nombre.trim().toLowerCase();
        return usuariosActivos().stream()
                .filter(u -> (u.getNombre() + " " + u.getApellido()).toLowerCase().contains(query)
                          || u.getNombre().toLowerCase().contains(query)
                          || u.getApellido().toLowerCase().contains(query))
                .toList();
    }

    private List<User> usuariosActivos() {
        return userRepository.findAll().stream()
                .filter(u -> u.getActivo() != null && u.getActivo())
                .filter(u -> u.getRol() != null && "USER".equals(u.getRol().name()))
                .toList();
    }
}