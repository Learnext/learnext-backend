package edu.ptithcm.learnnextbackend.modules.learning;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.enrollment.dto.response.EnrollmentResponse;
import edu.ptithcm.learnnextbackend.modules.learning.dto.request.CompleteLessonRequest;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LearningAccessResponse;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LessonProgressResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningController {
    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollments(
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(learningService.getEnrollments(userId)));
    }

    @GetMapping("/courses/{courseId}/access")
    public ResponseEntity<ApiResponse<LearningAccessResponse>> getCourseAccess(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(ApiResponse.success(learningService.getCourseAccess(userId, courseId)));
    }

    @PostMapping("/courses/{courseId}/complete")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> completeLesson(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID courseId,
            @RequestBody @Valid CompleteLessonRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(learningService.completeLesson(userId, courseId, request)));
    }
}
