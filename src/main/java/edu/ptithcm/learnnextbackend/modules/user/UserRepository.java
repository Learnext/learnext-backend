package edu.ptithcm.learnnextbackend.modules.user;

import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID>  {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
