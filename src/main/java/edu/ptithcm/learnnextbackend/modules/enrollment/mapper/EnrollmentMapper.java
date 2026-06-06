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
                .thumbnailUrl(enrollment.getCourse().getThumbnailUrl())
                .categoryName(enrollment.getCourse().getCategory().getName())
                .instructorName(enrollment.getCourse().getInstructor().getFullName())
                .progress(0)
                .createdAt(enrollment.getCreatedAt())
                .build();
    }
}
