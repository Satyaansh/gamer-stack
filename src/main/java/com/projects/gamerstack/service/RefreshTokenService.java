package com.projects.gamerstack.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.gamerstack.exception.GamerStackException;
import com.projects.gamerstack.model.RefreshToken;
import com.projects.gamerstack.repository.RefreshTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;

    RefreshToken generaRefreshToken() {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());
        return refreshTokenRepository.save(refreshToken);

    }

    void validateRefreshToken(String token) {

        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new GamerStackException("Invalid Refresh token"));

    }

    public void deleteRefreshToken(String token) {

        refreshTokenRepository.deleteByToken(token);
        
    }
}
