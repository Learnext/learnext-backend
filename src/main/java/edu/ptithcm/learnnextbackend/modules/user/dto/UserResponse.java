package edu.ptithcm.learnnextbackend.modules.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}