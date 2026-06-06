package edu.ptithcm.learnnextbackend.modules.learning;

import edu.ptithcm.learnnextbackend.modules.enrollment.dto.response.EnrollmentResponse;
import edu.ptithcm.learnnextbackend.modules.learning.dto.request.CompleteLessonRequest;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LearningAccessResponse;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LearningLessonResponse;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LessonProgressResponse;

import java.util.List;
import java.util.UUID;

public interface LearningService {
    List<EnrollmentResponse> getEnrollments(UUID userId);

    LearningAccessResponse getCourseAccess(UUID userId, UUID courseId);

    List<LearningLessonResponse> getCourseLessons(UUID userId, UUID courseId);

    LessonProgressResponse completeLesson(UUID userId, UUID courseId, CompleteLessonRequest request);
}
