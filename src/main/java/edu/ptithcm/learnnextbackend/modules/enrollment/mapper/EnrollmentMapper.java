package edu.ptithcm.learnnextbackend.modules.enrollment.mapper;

import edu.ptithcm.learnnextbackend.modules.enrollment.dto.response.EnrollmentResponse;
import edu.ptithcm.learnnextbackend.modules.enrollment.entity.Enrollment;

public final class EnrollmentMapper {
    private EnrollmentMapper() {
    }

    public static EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .createdAt(enrollment.getCreatedAt())
                .build();
    }
}
