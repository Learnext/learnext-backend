package edu.ptithcm.learnnextbackend.modules.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String phone;
}
