package edu.ptithcm.learnnextbackend.modules.user.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.utils.PasswordUtil;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.UserService;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import edu.ptithcm.learnnextbackend.modules.user.dto.request.CreateUserRequest;
import edu.ptithcm.learnnextbackend.modules.user.dto.request.CreateUserResponse;
import edu.ptithcm.learnnextbackend.modules.user.dto.response.UpdateUserRequest;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        String passwordHash = PasswordUtil.hash(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .fullName(request.getFullName())
                // .avatarUrl(request.getAvatarUrl())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return toCreateUserResponse(savedUser);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public CreateUserResponse getById(UUID id) {
        return toCreateUserResponse(getActiveUserById(id));
    }

    @Override
    public List<CreateUserResponse> getAllUsers() {
        return userRepository.findAllByStatusNot(UserStatus.DELETED)
                .stream()
                .map(this::toCreateUserResponse)
                .toList();
    }

    @Override
    public CreateUserResponse update(UUID id, UpdateUserRequest request) {
        User user = getActiveUserById(id);
        user.setFullName(request.getFullName());
        User savedUser = userRepository.save(user);
        return toCreateUserResponse(savedUser);
    }

    @Override
    public void delete(UUID id) {
        User user = getActiveUserById(id);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    private User getActiveUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BadRequestException("User not found");
        }

        return user;
    }

    private CreateUserResponse toCreateUserResponse(User user) {
        return CreateUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .build();
    }
}