package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.domain.model.Player;
import com.prode.domain.port.outbound.PlayerRepository;
import com.prode.infrastructure.adapter.outbound.persistence.entity.PlayerEntity;
import com.prode.infrastructure.adapter.outbound.persistence.mapper.PlayerMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PlayerRepositoryAdapter implements PlayerRepository {

    private final JpaPlayerRepository jpaPlayerRepository;

    public PlayerRepositoryAdapter(JpaPlayerRepository jpaPlayerRepository) {
        this.jpaPlayerRepository = jpaPlayerRepository;
    }

    @Override
    public List<Player> findAll() {
        return jpaPlayerRepository.findAll().stream()
                .map(PlayerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> findAllActive() {
        return jpaPlayerRepository.findByActivoTrue().stream()
                .map(PlayerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Player> findAllActive(Pageable pageable) {
        return jpaPlayerRepository.findByActivoTrue(pageable)
                .map(PlayerMapper::toDomain);
    }

    @Override
    public Optional<Player> findById(Long id) {
        return jpaPlayerRepository.findById(id)
                .map(PlayerMapper::toDomain);
    }

    @Override
    public List<Player> findByTeamId(Long teamId) {
        return jpaPlayerRepository.findActiveByTeamId(teamId).stream()
                .map(PlayerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> findByNombreContainingIgnoreCase(String nombre) {
        return jpaPlayerRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(PlayerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Player save(Player player) {
        PlayerEntity entity = PlayerMapper.toEntity(player);
        PlayerEntity saved = jpaPlayerRepository.save(entity);
        return PlayerMapper.toDomain(saved);
    }

    @Override
    public Player update(Player player) {
        return save(player);
    }

    @Override
    public List<Player> findUnassigned() {
        return jpaPlayerRepository.findUnassigned().stream()
                .map(PlayerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> findUnassignedOrByTeamId(Long teamId) {
        return jpaPlayerRepository.findUnassignedOrByTeamId(teamId).stream()
                .map(PlayerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaPlayerRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaPlayerRepository.existsById(id);
    }
}
