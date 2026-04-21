package edu.ptithcm.learnnextbackend.modules.user;

import edu.ptithcm.learnnextbackend.modules.user.dto.CreateUserRequest;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import java.util.UUID;
import java.util.List;
public interface UserService {
    User createUser(CreateUserRequest request);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    User getById(UUID id);
    List<User> getAllUsers();
    User update(UUID id, String fullName, String avatarUrl);
    void delete(UUID id);
}
