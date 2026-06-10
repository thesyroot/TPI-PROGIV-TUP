package com.prode.infrastructure.security;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.prode.infrastructure.adapter.outbound.persistence.entity.RevokedTokenEntity;
import com.prode.infrastructure.adapter.outbound.persistence.repository.JpaRevokedTokenRepository;

@Service
public class TokenBlacklistService {

    private final JpaRevokedTokenRepository revokedTokenRepository;

    public TokenBlacklistService(JpaRevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    public void blacklistToken(String token) {
        if (!isBlacklisted(token)) {
            RevokedTokenEntity revokedToken = new RevokedTokenEntity();
            revokedToken.setToken(token);
            revokedToken.setFechaRevocacion(LocalDateTime.now());
            revokedTokenRepository.save(revokedToken);
        }
    }

    public boolean isBlacklisted(String token) {
        return revokedTokenRepository.existsByToken(token);
    }
}