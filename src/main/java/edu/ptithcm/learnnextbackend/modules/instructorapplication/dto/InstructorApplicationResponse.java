package edu.ptithcm.learnnextbackend.modules.instructorapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorApplicationResponse {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String email;
    private String qualification;
    private String phone;
    private String certificateUrl;
    private String bio;
    private String status;
    private String reviewNote;
    private LocalDateTime createdAt;
}
