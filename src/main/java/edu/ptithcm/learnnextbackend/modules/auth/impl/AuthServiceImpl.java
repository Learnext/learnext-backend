package edu.ptithcm.learnnextbackend.modules.auth.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.ptithcm.learnnextbackend.modules.auth.AuthService;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.LoginRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.RefreshRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.RegisterRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.RegisterResponse;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.TokenResponse;
import edu.ptithcm.learnnextbackend.modules.auth.entity.RefreshToken;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import edu.ptithcm.learnnextbackend.modules.auth.JwtService;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenServiceImpl refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest req) {
        String normalizedEmail = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(req.getPassword());
        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordHash)
                .fullName(req.getFullName())
                .status(UserStatus.ACTIVE)
                .build();
        try {
            User savedUser = userRepository.save(user);

            return RegisterResponse.builder()
                    .id(savedUser.getId().toString())
                    .email(savedUser.getEmail())
                    .name(savedUser.getFullName())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Email already exists");
        }
    }

    @Override
    public TokenResponse login(LoginRequest req) {
        String normalizedEmail = req.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
                
        if (user.getStatus() == UserStatus.BANNED || user.getStatus() == UserStatus.DELETED) {
            throw new BadRequestException("User account is disabled");
        }
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        return buildTokenResponse(user);
    }

    @Override
    public TokenResponse refresh(RefreshRequest req) {
        RefreshToken oldToken = refreshTokenService.rotateToken(req.getRefreshToken());
        return buildTokenResponse(oldToken.getUser());
    }
 
    @Override
    public void logout(String rawRefreshToken) {
        refreshTokenService.revokeToken(rawRefreshToken);
    }
 
    @Override
    public void logoutAll(UUID userId) {
        refreshTokenService.revokeAllTokensOfUser(userId);
    }

    // ── Private helper ────────────────────────────────────────────────────────
    
    private TokenResponse buildTokenResponse(User user) {
        String accessToken  = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user);
 
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtService.getAccessTokenExpiry())
                .build();
    }
}
