package edu.ptithcm.learnnextbackend.modules.auth;

import java.util.UUID;

import edu.ptithcm.learnnextbackend.modules.auth.dto.request.LoginRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.RefreshRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.RegisterRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.RegisterResponse;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.TokenResponse;


public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshRequest request);
    void logout(String rawRefreshToken);
    void logoutAll(UUID userId);
}
