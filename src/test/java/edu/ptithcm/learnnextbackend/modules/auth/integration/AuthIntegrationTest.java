package edu.ptithcm.learnnextbackend.modules.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ptithcm.learnnextbackend.config.TestJacksonConfig;
import edu.ptithcm.learnnextbackend.config.TestRedisConfig;
import edu.ptithcm.learnnextbackend.modules.auth.RefreshTokenRepository;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.LoginRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.RefreshRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.request.RegisterRequest;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.TokenResponse;
import edu.ptithcm.learnnextbackend.modules.auth.entity.RefreshToken;
import edu.ptithcm.learnnextbackend.modules.user.UserProfileRepository;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestRedisConfig.class, TestJacksonConfig.class})
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;


    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        refreshTokenRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    // =========================
    // REGISTER
    // =========================

    @Test
    void register_should_success() throws Exception {

        RegisterRequest req = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("password123")
                .fullName("Test User")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.name").value("Test User"));

        assertThat(userRepository.existsByEmail("test@gmail.com")).isTrue();
    }

    @Test
    void register_should_return_400_when_email_exists() throws Exception {

        RegisterRequest req = RegisterRequest.builder()
                .email("duplicate@gmail.com")
                .password("password123")
                .fullName("User 1")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_should_return_400_when_email_invalid() throws Exception {

        RegisterRequest req = RegisterRequest.builder()
                .email("invalid-email")
                .password("password123")
                .fullName("Test User")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_should_return_400_when_password_too_short() throws Exception {

        RegisterRequest req = RegisterRequest.builder()
                .email("short@gmail.com")
                .password("123")
                .fullName("Short Password")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // =========================
    // LOGIN
    // =========================

    @Test
    void login_should_success() throws Exception {

        RegisterRequest register = RegisterRequest.builder()
                .email("login@gmail.com")
                .password("password123")
                .fullName("Login User")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = LoginRequest.builder()
                .email("login@gmail.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.accessTokenExpiresIn").value(5));
    }

    @Test
    void login_should_return_400_when_password_wrong() throws Exception {

        RegisterRequest register = RegisterRequest.builder()
                .email("wrong@gmail.com")
                .password("password123")
                .fullName("Wrong Password")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = LoginRequest.builder()
                .email("wrong@gmail.com")
                .password("wrong-password")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    // =========================
    // REFRESH TOKEN
    // =========================

    @Test
    void refresh_should_success() throws Exception {

        TokenResponse tokenResponse = registerAndLogin(
                "refresh@gmail.com",
                "password123",
                "Refresh User"
        );

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken(tokenResponse.getRefreshToken());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void refresh_should_fail_when_reuse_old_refresh_token() throws Exception {

        TokenResponse tokenResponse = registerAndLogin(
                "reuse@gmail.com",
                "password123",
                "Reuse User"
        );

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken(tokenResponse.getRefreshToken());

        // refresh lần 1 -> success
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk());

        // reuse refresh token cũ -> fail
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    // =========================
    // LOGOUT
    // =========================

    @Test
    void logout_should_success() throws Exception {

        TokenResponse tokenResponse = registerAndLogin(
                "logout@gmail.com",
                "password123",
                "Logout User"
        );

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken(tokenResponse.getRefreshToken());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization",
                                        "Bearer " + tokenResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_should_revoke_refresh_token() throws Exception {

        TokenResponse tokenResponse = registerAndLogin(
                "revoke@gmail.com",
                "password123",
                "Revoke User"
        );

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken(tokenResponse.getRefreshToken());

        // logout
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization",
                                        "Bearer " + tokenResponse.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        // verify DB state
        String hash = hashToken(tokenResponse.getRefreshToken());

        RefreshToken token = refreshTokenRepository
                        .findByTokenHash(hash)
                        .orElseThrow();

        assertThat(token.isRevoked()).isTrue();
        
        // dùng lại refresh token -> fail
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    // =========================
    // SECURITY / JWT
    // =========================

    @Test
    void logout_all_should_return_401_when_no_token() throws Exception {

        mockMvc.perform(post("/api/v1/auth/logout-all"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_all_should_success_when_valid_jwt() throws Exception {

        TokenResponse tokenResponse = registerAndLogin(
                "jwt@gmail.com",
                "password123",
                "Jwt User"
        );

        mockMvc.perform(post("/api/v1/auth/logout-all")
                        .header("Authorization",
                                "Bearer " + tokenResponse.getAccessToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_all_should_return_401_when_invalid_token() throws Exception {

        mockMvc.perform(post("/api/v1/auth/logout-all")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_all_should_return_401_when_malformed_token() throws Exception {

        mockMvc.perform(post("/api/v1/auth/logout-all")
                        .header("Authorization", "Bearer abc.def"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_all_should_return_401_when_token_expired() throws Exception {
            TokenResponse tokenResponse = registerAndLogin(
                            "expired@gmail.com",
                            "password123",
                            "Expired User");

            //chờ token hết hạn
            Thread.sleep(6000);

            mockMvc.perform(post("/api/v1/auth/logout-all")
                            .header(
                                            "Authorization",
                                            "Bearer " + tokenResponse.getAccessToken()))
                            .andExpect(status().isUnauthorized());
    }

    // =========================
    // HELPER
    // =========================

    private TokenResponse registerAndLogin(
            String email,
            String password,
            String fullName
    ) throws Exception {

        RegisterRequest register = RegisterRequest.builder()
                .email(email)
                .password(password)
                .fullName(fullName)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String loginResponse = result
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(loginResponse, TokenResponse.class);
    }

    private String hashToken(String rawToken) throws Exception {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                            rawToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
    }
}
