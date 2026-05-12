package edu.ptithcm.learnnextbackend.modules.auth;

import edu.ptithcm.learnnextbackend.modules.auth.dto.request.*;
import edu.ptithcm.learnnextbackend.modules.auth.dto.response.*;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
 
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }
 
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
 
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }
 
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody @Valid RefreshRequest req) {
        authService.logout(req.getRefreshToken());
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Logged out successfully")
                .build());
    }

    // Logout tất cả devices — yêu cầu access token hợp lệ
    @PostMapping("/logout-all")
    public ResponseEntity<MessageResponse> logoutAll(@AuthenticationPrincipal UUID userId) {
        authService.logoutAll(userId);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Logged out from all devices successfully")
                .build());
    }
}
 