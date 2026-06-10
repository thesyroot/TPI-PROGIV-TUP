package com.prode.infrastructure.security;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prode.infrastructure.adapter.outbound.persistence.entity.RefreshTokenEntity;
import com.prode.infrastructure.adapter.outbound.persistence.repository.JpaRefreshTokenRepository;
import com.prode.infrastructure.adapter.outbound.persistence.repository.JpaUserRepository;
import com.prode.shared.exception.BusinessException;

@Service
public class RefreshTokenService {

    private final JpaRefreshTokenRepository refreshTokenRepository;
    private final JpaUserRepository userRepository;

    public RefreshTokenService(JpaRefreshTokenRepository refreshTokenRepository, JpaUserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshTokenEntity createRefreshToken(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        refreshTokenRepository.deleteByUsuario(user);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUsuario(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setFechaExpiracion(Instant.now().plusMillis(604800000L)); // 7 Días

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getFechaExpiracion().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new BusinessException("El Refresh token ha expirado. Inicie sesión nuevamente.");
        }
        return token;
    }

    @Transactional
    public void deleteByUserEmail(String email) {
        userRepository.findByEmail(email).ifPresent(refreshTokenRepository::deleteByUsuario);
    }
}