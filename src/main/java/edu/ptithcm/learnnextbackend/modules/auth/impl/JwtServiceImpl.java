package edu.ptithcm.learnnextbackend.modules.auth.impl;

import edu.ptithcm.learnnextbackend.modules.auth.JwtService;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class JwtServiceImpl implements JwtService {
    private SecretKey secretKey;
    private long accessTokenExpiry;

    public JwtServiceImpl(@Value("${jwt.secret:}") String secret,
            @Value("${jwt.access-token-expiry:900}") long accessTokenExpiry) {
        if (secret == null || secret.isBlank() || secret.length() < 32) {
            throw new BadRequestException("Missing or invalid jwt.secret property (must be >=32 chars). Set 'jwt.secret' in application-* properties or JWT_SECRET env var.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
    }
 
    @Override
    public String generateAccessToken(UUID userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpiry)))
                .signWith(secretKey)
                .compact();
    }
 
    @Override
    public Claims validateAndExtract(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
 
    @Override
    public String extractUserId(String token) {
        return validateAndExtract(token).getSubject();
    }
 
    @Override
    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }
}
