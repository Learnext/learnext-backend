package edu.ptithcm.learnnextbackend.modules.admin;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminAuthController {
    private final String adminEmail;
    private final String adminPassword;
    private final String adminKey;

    public AdminAuthController(
            @Value("${admin.email:admin@learnext.local}") String adminEmail,
            @Value("${admin.password:admin123}") String adminPassword,
            @Value("${admin.key:dev-admin-key}") String adminKey
    ) {
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminKey = adminKey;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        if (!adminEmail.equalsIgnoreCase(request.getEmail().trim()) || !adminPassword.equals(request.getPassword())) {
            throw new BadRequestException("Invalid admin credentials");
        }
        return ResponseEntity.ok(ApiResponse.success(new LoginResponse(adminKey)));
    }

    @Getter
    @Setter
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String adminKey;
    }
}
