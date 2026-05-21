package edu.ptithcm.learnnextbackend.modules.auth.unit;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.auth.JwtService;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.LoginRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.RegisterRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.RegisterResponse;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.TokenResponse;
import edu.ptithcm.learnnextbackend.modules.auth.impl.AuthServiceImpl;
import edu.ptithcm.learnnextbackend.modules.auth.impl.RefreshTokenServiceImpl;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import edu.ptithcm.learnnextbackend.modules.user.impl.UserProfileServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserProfileServiceImpl userProfileService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
       void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .passwordHash("hashed-password")
                .fullName("Test User")
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void register_should_success() {
        RegisterRequest req = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("password123")
                .fullName("Test User")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        RegisterResponse response = authService.register(req);

        assertEquals("test@gmail.com", response.getEmail());
        assertEquals("Test User", response.getName());

        verify(userRepository).save(any(User.class));
        verify(userProfileService).createProfileRegister(any(), any());
    }

    @Test
    void register_should_throw_when_email_exists() {
        RegisterRequest req = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("password123")
                .fullName("Test User")
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
    }

    @Test
    void login_should_success() {
        LoginRequest req = LoginRequest.builder()
                .email("test@gmail.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        when(jwtService.generateAccessToken(any(), any()))
                .thenReturn("access-token");

        when(jwtService.getAccessTokenExpiry())
                .thenReturn(900L);

        when(refreshTokenService.createRefreshToken(any()))
                .thenReturn("refresh-token");

        TokenResponse response = authService.login(req);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_should_throw_when_password_invalid() {
        LoginRequest req = LoginRequest.builder()
                .email("test@gmail.com")
                .password("wrong-password")
                .build();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(req));
    }

    @Test
    void login_should_throw_when_user_banned() {
        user.setStatus(UserStatus.BANNED);

        LoginRequest req = LoginRequest.builder()
                .email("test@gmail.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.login(req));
    }
}
