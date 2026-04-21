package edu.ptithcm.learnnextbackend.modules.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.ptithcm.learnnextbackend.modules.user.dto.CreateUserRequest;
import edu.ptithcm.learnnextbackend.modules.user.dto.UserResponse;
import edu.ptithcm.learnnextbackend.modules.user.dto.UpdateUserRequest;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(toResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                toResponse(userService.getById(id))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request
    ) {

        User user = userService.update(
                id,
                request.getFullName(),
                request.getAvatarUrl()
        );

        return ResponseEntity.ok(toResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}