package edu.ptithcm.learnnextbackend.modules.user.impl;

import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.UserService;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import edu.ptithcm.learnnextbackend.modules.user.dto.CreateUserRequest;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .fullName(request.getFullName())
                .avatarUrl(request.getAvatarUrl())
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String fullName, String avatarUrl) {
        User user = getById(id);
        user.setFullName(fullName);
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {

        User user = getById(id);

        userRepository.delete(user);
    }
}