package edu.ptithcm.learnnextbackend.modules.user;

import edu.ptithcm.learnnextbackend.modules.user.dto.CreateUserRequest;
import edu.ptithcm.learnnextbackend.modules.user.dto.CreateUserResponse;
import edu.ptithcm.learnnextbackend.modules.user.dto.UpdateUserRequest;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;

import java.util.UUID;
import java.util.List;
public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    CreateUserResponse getById(UUID id);
    List<CreateUserResponse> getAllUsers();
    CreateUserResponse update(UUID id, UpdateUserRequest request);
    void delete(UUID id);
}
