package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.course.dto.request.InstructorCourseRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructor/courses")
public class InstructorCourseController {
    private final InstructorCourseService instructorCourseService;

    public InstructorCourseController(InstructorCourseService instructorCourseService) {
        this.instructorCourseService = instructorCourseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getOwnCourses(
            @RequestHeader("X-Instructor-Id") UUID instructorId
    ) {
        return ResponseEntity.ok(ApiResponse.success(instructorCourseService.getOwnCourses(instructorId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @RequestBody @Valid InstructorCourseRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(instructorCourseService.createCourse(instructorId, request)));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @RequestBody @Valid InstructorCourseRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                instructorCourseService.updateCourse(instructorId, courseId, request)
        ));
    }

    @PatchMapping("/{courseId}/publish")
    public ResponseEntity<ApiResponse<CourseResponse>> publishCourse(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(ApiResponse.success(instructorCourseService.publishCourse(instructorId, courseId)));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> archiveCourse(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId
    ) {
        instructorCourseService.archiveCourse(instructorId, courseId);
        return ResponseEntity.noContent().build();
    }
}
