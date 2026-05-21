package edu.ptithcm.learnnextbackend.modules.auth.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.ptithcm.learnnextbackend.modules.auth.RefreshTokenService;
import edu.ptithcm.learnnextbackend.modules.auth.entity.RefreshToken;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.auth.RefreshTokenRepository;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    private RefreshTokenRepository repository;

    @Autowired
    private StringRedisTemplate redis;
 
    @Value("${jwt.refresh-token-expiry:604800}")
    private long refreshTokenExpiry;
 
    private static final String REDIS_PREFIX  = "rt:";
    private static final SecureRandom RANDOM  = new SecureRandom();
 
    @Override
    public String createRefreshToken(User user) {
        String rawToken = generateRawToken();
        String hash = hashToken(rawToken);
 
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(refreshTokenExpiry))
                .build();
 
        repository.save(entity);
        cacheInRedis(hash, user.getId().toString());
 
        return rawToken; 
    }
 
    @Override
    public RefreshToken rotateToken(String rawToken) {
        String hash = hashToken(rawToken);
 
        RefreshToken token = loadToken(hash);
 
        if (!token.isValid()) {
            if (token.isRevoked()) {
                log.warn("[Security] Reuse refresh token detected for user {}", token.getUser().getId());
                repository.revokeAllByUserId(token.getUser().getId());
            }
            throw new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
 
        token.setRevoked(true);
        repository.save(token);
        evictFromRedis(hash);
 
        return token;
    }
 
    @Override
    public void revokeToken(String rawToken) {
        String hash = hashToken(rawToken);
        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn"));
        if (!token.isValid()) {
            throw new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
        token.setRevoked(true);
        repository.save(token);
        evictFromRedis(hash);
    }
 
    @Override
    public void revokeAllTokensOfUser(UUID userId) {
        repository.revokeAllByUserId(userId);
        log.info("Revoked all refresh tokens for user {}", userId);
    }
 
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredTokens() {
        int deleted = repository.deleteExpiredAndRevoked();
        log.info("Cleaned up {} expired/revoked refresh tokens", deleted);
    }
 
 
    /**
     * Redis fast-path: nếu cache hit thì không cần query DB lần đầu.
     * Nếu miss (TTL hết hoặc bị evict) thì fallback sang DB.
     */
    private RefreshToken loadToken(String hash) {
        String cachedUserId = redis.opsForValue().get(REDIS_PREFIX + hash);
 
        if (cachedUserId == null) {
            log.debug("Redis cache miss for refresh token hash, falling back to DB");
        }
 
        return repository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Refresh token không tồn tại"));
    }
 
    private String generateRawToken() {
        byte[] bytes = new byte[64];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
 
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hash token thất bại", e);
        }
    }
 
    private void cacheInRedis(String hash, String userId) {
        redis.opsForValue().set(REDIS_PREFIX + hash, userId, refreshTokenExpiry, TimeUnit.SECONDS);
    }
 
    private void evictFromRedis(String hash) {
        redis.delete(REDIS_PREFIX + hash);
    }
}
