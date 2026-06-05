package edu.ptithcm.learnnextbackend.modules.activation;

import edu.ptithcm.learnnextbackend.modules.activation.dto.request.ActivateCourseRequest;
import edu.ptithcm.learnnextbackend.modules.enrollment.dto.response.EnrollmentResponse;

import java.util.UUID;

public interface ActivationService {
    EnrollmentResponse activateCourse(UUID userId, ActivateCourseRequest request);
}
