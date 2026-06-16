package com.prode.domain.port.outbound;

import com.prode.application.dto.response.RankingEntryResponse;
import com.prode.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    User update(User user);
    boolean existsByEmail(String email);
    List<RankingEntryResponse> findRankingGlobal();
    List<RankingEntryResponse> findRankingByUserIds(List<Long> userIds);
}
