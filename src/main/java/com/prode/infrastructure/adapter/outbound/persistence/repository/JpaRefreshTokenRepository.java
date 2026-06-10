package com.prode.infrastructure.adapter.outbound.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.RefreshTokenEntity;
import com.prode.infrastructure.adapter.outbound.persistence.entity.UserEntity;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUsuario(UserEntity usuario);
}