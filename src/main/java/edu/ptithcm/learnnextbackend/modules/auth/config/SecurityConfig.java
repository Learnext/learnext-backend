package edu.ptithcm.learnnextbackend.modules.auth.config;

import edu.ptithcm.learnnextbackend.modules.auth.security.JwtAuthFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
 
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final AuthenticationTrustResolver TRUST_RESOLVER = new AuthenticationTrustResolverImpl();

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/courses/*/reviews",
                                "/api/v1/courses/*/comments"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/courses",
                                "/api/v1/courses/*",
                                "/api/v1/categories",
                                "/api/v1/instructor/courses",
                                "/api/v1/instructor/courses/*",
                                "/api/v1/instructor/courses/*/publish",
                                "/api/v1/uploads/signed-url",
                                "/api/v1/admin/orders",
                                "/api/v1/admin/orders/*/confirm",
                                "/api/v1/admin/orders/*/reject",
                                "/api/v1/support/leads",
                                "/api/v1/admin/support/leads"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Spring Security 7 mặc định dùng Http403ForbiddenEntryPoint — chưa xác thực vẫn 403.
                // 401 cho thiếu / sai JWT; 403 khi đã đăng nhập nhưng không đủ quyền.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            Authentication authentication =
                                    SecurityContextHolder.getContext().getAuthentication();
                            if (authentication == null || TRUST_RESOLVER.isAnonymous(authentication)) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            } else {
                                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            }
                        }))
                // Đặt JwtAuthFilter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
