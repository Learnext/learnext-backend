package edu.ptithcm.learnnextbackend.modules.auth.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import edu.ptithcm.learnnextbackend.modules.user.entity.User;


@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token_user", columnList = "user_id"),
    @Index(name = "idx_refresh_token_hash", columnList = "token_hash", unique = true)
})
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
 
    // Lưu hash(token) thay vì raw token để tránh lộ DB
    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 
    @Column(nullable = false)
    private Instant expiresAt;
 
    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;
 
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
 
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
 
    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
