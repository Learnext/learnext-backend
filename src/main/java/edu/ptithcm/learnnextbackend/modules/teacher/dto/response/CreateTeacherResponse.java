package edu.ptithcm.learnnextbackend.modules.teacher.dto.response;

import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeacherResponse {

    private String id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private TeacherStatus status;
    private LocalDateTime createdAt;
}