package edu.ptithcm.learnnextbackend.modules.user;

import edu.ptithcm.learnnextbackend.modules.user.entity.UserProfile;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUser(User user);
}
