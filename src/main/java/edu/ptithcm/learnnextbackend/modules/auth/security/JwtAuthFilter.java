package edu.ptithcm.learnnextbackend.modules.auth.security;

import edu.ptithcm.learnnextbackend.modules.auth.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.UUID;
import java.io.IOException;

 
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
 
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
 
        String token = extractToken(request);
 
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var claims = jwtService.validateAndExtract(token);
                UUID userId = UUID.fromString(claims.getSubject());
 
                // Tạo Authentication object — không cần load UserDetails từ DB
                // vì thông tin đã được embed trong JWT
                var auth = new UsernamePasswordAuthenticationToken(
                        userId,          // principal
                        null,            // credentials
                        List.of()        // authorities — thêm roles nếu cần
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
 
                SecurityContextHolder.getContext().setAuthentication(auth);
 
            } catch (JwtException e) {
                // Token invalid hoặc expired — không set auth, tiếp tục filter chain
                // Spring Security sẽ trả 401 nếu endpoint yêu cầu auth
                log.debug("JWT validation failed: {}", e.getMessage());
            }
        }
 
        filterChain.doFilter(request, response);
    }
 
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
