package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.course.dto.request.CourseSearchRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@Validated
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> searchCourses(
            @Valid @ModelAttribute CourseSearchRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(courseService.searchCourses(request)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> searchCoursesAlias(
            @Valid @ModelAttribute CourseSearchRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(courseService.searchCourses(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getPublishedCourse(id)));
    }
}
