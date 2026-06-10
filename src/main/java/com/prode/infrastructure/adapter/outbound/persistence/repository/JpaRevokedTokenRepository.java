package com.prode.infrastructure.adapter.outbound.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prode.infrastructure.adapter.outbound.persistence.entity.RevokedTokenEntity;

public interface JpaRevokedTokenRepository extends JpaRepository<RevokedTokenEntity, Long> {
    boolean existsByToken(String token);
}