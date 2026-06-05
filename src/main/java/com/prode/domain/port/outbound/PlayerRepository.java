package com.prode.domain.port.outbound;

import com.prode.domain.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    List<Player> findAll();
    List<Player> findAllActive();
    Page<Player> findAllActive(Pageable pageable);
    Optional<Player> findById(Long id);
    List<Player> findByTeamId(Long teamId);
    List<Player> findByNombreContainingIgnoreCase(String nombre);
    List<Player> findUnassigned();
    List<Player> findUnassignedOrByTeamId(Long teamId);
    Player save(Player player);
    Player update(Player player);
    void deleteById(Long id);
    boolean existsById(Long id);
}
