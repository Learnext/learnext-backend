package edu.ptithcm.learnnextbackend.modules.instructorapplication;

import edu.ptithcm.learnnextbackend.modules.instructorapplication.dto.InstructorApplicationResponse;
import edu.ptithcm.learnnextbackend.modules.instructorapplication.entity.InstructorApplication;

public final class InstructorApplicationMapper {
    private InstructorApplicationMapper() {
    }

    public static InstructorApplicationResponse toResponse(InstructorApplication application) {
        return InstructorApplicationResponse.builder()
                .id(application.getId())
                .userId(application.getUser().getId())
                .fullName(application.getUser().getFullName())
                .email(application.getUser().getEmail())
                .qualification(application.getQualification())
                .phone(application.getPhone())
                .certificateUrl(application.getCertificateUrl())
                .bio(application.getBio())
                .status(application.getStatus().name())
                .reviewNote(application.getReviewNote())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
