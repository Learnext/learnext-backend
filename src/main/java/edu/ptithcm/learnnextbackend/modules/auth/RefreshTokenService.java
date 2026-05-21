package edu.ptithcm.learnnextbackend.modules.auth;

import java.util.UUID;

import edu.ptithcm.learnnextbackend.modules.auth.entity.RefreshToken;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;

public interface RefreshTokenService {
    String createRefreshToken(User user);
    RefreshToken rotateToken(String rawToken);
    void revokeToken(String rawToken);
    void revokeAllTokensOfUser(UUID userId);
}
