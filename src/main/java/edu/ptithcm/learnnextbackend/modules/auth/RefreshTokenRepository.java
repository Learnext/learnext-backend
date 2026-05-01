package edu.ptithcm.learnnextbackend.modules.auth;

import edu.ptithcm.learnnextbackend.modules.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
 
    Optional<RefreshToken> findByTokenHash(String tokenHash);
 
    // Revoke tất cả token của user — dùng khi logout all devices
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.user.id = :userId AND t.revoked = false")
    int revokeAllByUserId(UUID userId);
 
    // Xóa token hết hạn — dùng trong scheduled cleanup job
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < CURRENT_TIMESTAMP OR t.revoked = true")
    int deleteExpiredAndRevoked();
}
