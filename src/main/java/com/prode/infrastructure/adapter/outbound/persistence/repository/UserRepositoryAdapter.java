package com.prode.infrastructure.adapter.outbound.persistence.repository;

import com.prode.application.dto.response.RankingEntryResponse;
import com.prode.domain.model.User;
import com.prode.domain.port.outbound.UserRepository;
import com.prode.infrastructure.adapter.outbound.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream().map(UserMapper::toDomain).toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        return UserMapper.toDomain(jpaUserRepository.save(UserMapper.toEntity(user)));
    }

    @Override
    public User update(User user) {
        return save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public List<RankingEntryResponse> findRankingGlobal() {
        return mapRawToRanking(jpaUserRepository.findRankingGlobalRaw());
    }

    @Override
    public List<RankingEntryResponse> findRankingByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        return mapRawToRanking(jpaUserRepository.findRankingByUserIdsRaw(userIds));
    }

    private List<RankingEntryResponse> mapRawToRanking(List<Object[]> rows) {
        AtomicInteger pos = new AtomicInteger(1);
        return rows.stream().map(row -> {
            RankingEntryResponse entry = new RankingEntryResponse();
            entry.setPosicion(pos.getAndIncrement());
            entry.setUserId(((Number) row[0]).longValue());
            entry.setNombre((String) row[1]);
            entry.setApellido((String) row[2]);
            entry.setPuntosTotal(row[3] != null ? ((Number) row[3]).intValue() : 0);
            entry.setPlenos(row[4] != null ? ((Number) row[4]).longValue() : 0L);
            entry.setAciertos(row[5] != null ? ((Number) row[5]).longValue() : 0L);
            return entry;
        }).toList();
    }
}
