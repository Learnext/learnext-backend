package edu.ptithcm.learnnextbackend.modules.enrollment.dto.response;

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
public class EnrollmentResponse {
    private UUID id;
    private UUID courseId;
    private String courseTitle;
    private LocalDateTime createdAt;
}
