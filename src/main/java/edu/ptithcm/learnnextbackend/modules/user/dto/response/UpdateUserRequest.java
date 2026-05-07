package edu.ptithcm.learnnextbackend.modules.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String avatarUrl;
}