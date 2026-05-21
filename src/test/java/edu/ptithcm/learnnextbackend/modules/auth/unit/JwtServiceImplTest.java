package edu.ptithcm.learnnextbackend.modules.auth.unit;

import edu.ptithcm.learnnextbackend.modules.auth.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceImplTest {
    private final JwtServiceImpl jwtService =
            new JwtServiceImpl(
                    "test-secret-key-12345678901234567890",
                    900
            );

    @Test
    void generate_and_validate_token_success() {
        UUID userId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId, "test@gmail.com");

        assertNotNull(token);

        Claims claims = jwtService.validateAndExtract(token);

        assertEquals(userId.toString(), claims.getSubject());
        assertEquals("test@gmail.com", claims.get("email"));
    }
}
