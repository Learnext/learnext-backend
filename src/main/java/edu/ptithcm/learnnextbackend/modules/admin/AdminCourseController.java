package edu.ptithcm.learnnextbackend.modules.admin;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.course.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/courses")
public class AdminCourseController {
    private final CourseRepository courseRepository;
    private final String adminKey;

    public AdminCourseController(
            CourseRepository courseRepository,
            @Value("${admin.key:dev-admin-key}") String adminKey
    ) {
        this.courseRepository = courseRepository;
        this.adminKey = adminKey;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses(
            @RequestHeader("X-Admin-Key") String providedKey
    ) {
        requireAdmin(providedKey);
        return ResponseEntity.ok(ApiResponse.success(courseRepository.findAll()
                .stream()
                .map(CourseMapper::toResponse)
                .toList()));
    }

    @PatchMapping("/{courseId}/approve")
    public ResponseEntity<ApiResponse<CourseResponse>> approve(
            @RequestHeader("X-Admin-Key") String providedKey,
            @PathVariable UUID courseId
    ) {
        requireAdmin(providedKey);
        Course course = requireCourse(courseId);
        course.setStatus(CourseStatus.PUBLISHED);
        return ResponseEntity.ok(ApiResponse.success(CourseMapper.toResponse(courseRepository.save(course))));
    }

    @PatchMapping("/{courseId}/reject")
    public ResponseEntity<ApiResponse<CourseResponse>> reject(
            @RequestHeader("X-Admin-Key") String providedKey,
            @PathVariable UUID courseId
    ) {
        requireAdmin(providedKey);
        Course course = requireCourse(courseId);
        course.setStatus(CourseStatus.REJECTED);
        return ResponseEntity.ok(ApiResponse.success(CourseMapper.toResponse(courseRepository.save(course))));
    }

    private Course requireCourse(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private void requireAdmin(String providedKey) {
        if (!adminKey.equals(providedKey)) {
            throw new BadRequestException("Invalid admin key");
        }
    }
}
