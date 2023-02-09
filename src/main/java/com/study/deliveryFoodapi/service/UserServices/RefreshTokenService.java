package com.study.deliveryFoodapi.service.UserServices;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.study.deliveryFoodapi.exception.RefreshTokenException;
import com.study.deliveryFoodapi.model.RefreshToken;
import com.study.deliveryFoodapi.model.User;
import com.study.deliveryFoodapi.repository.RefreshTokenRepository;
import com.study.deliveryFoodapi.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {
    @Value("${foodDely.jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {

        User user = userRepository.findById(userId).get();

        RefreshToken refreshTokenVeri = refreshTokenRepository.findByUserAndExpiryDateAfter(user, Instant.now());
        if (refreshTokenVeri != null) {
            return refreshTokenVeri;
        }

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Transactional
    public void deleteAllRefreshTokensExpired() {
        List<RefreshToken> tokens = refreshTokenRepository.findAll();

        if (!tokens.isEmpty()) {
            Instant now = Instant.now();

            tokens.forEach(token -> {
                if (now.isAfter(token.getExpiryDate())) {
                    refreshTokenRepository.deleteById(token.getId());
                }
            });
        }

    }
}
