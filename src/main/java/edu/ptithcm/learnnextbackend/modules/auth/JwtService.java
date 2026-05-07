package edu.ptithcm.learnnextbackend.modules.auth;

import java.util.UUID;

import io.jsonwebtoken.Claims;

public interface JwtService {
    String generateAccessToken(UUID userId, String email);
    Claims validateAndExtract(String token);
    String extractUserId(String token);
    long getAccessTokenExpiry();
}
