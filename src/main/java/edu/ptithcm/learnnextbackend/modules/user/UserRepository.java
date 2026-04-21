package edu.ptithcm.learnnextbackend.modules.user;

import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;

import java.util.UUID;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID>  {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllByStatusNot(UserStatus status);
}
