package edu.ptithcm.learnnextbackend.modules.course.content;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.ChapterResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.LessonRequest;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.LessonResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.SectionResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.TitleRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructor/courses/{courseId}/content")
public class CourseContentController {
    private final CourseContentService courseContentService;

    public CourseContentController(CourseContentService courseContentService) {
        this.courseContentService = courseContentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getContent(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(ApiResponse.success(courseContentService.getInstructorContent(instructorId, courseId)));
    }

    @PostMapping("/chapters")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @RequestBody @Valid TitleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseContentService.createChapter(instructorId, courseId, request.getTitle())));
    }

    @PatchMapping("/chapters/{chapterId}")
    public ResponseEntity<ApiResponse<ChapterResponse>> updateChapter(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @RequestBody @Valid TitleRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                courseContentService.updateChapter(instructorId, courseId, chapterId, request.getTitle())
        ));
    }

    @DeleteMapping("/chapters/{chapterId}")
    public ResponseEntity<Void> deleteChapter(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId
    ) {
        courseContentService.deleteChapter(instructorId, courseId, chapterId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/chapters/{chapterId}/sections")
    public ResponseEntity<ApiResponse<SectionResponse>> createSection(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @RequestBody @Valid TitleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseContentService.createSection(
                        instructorId, courseId, chapterId, request.getTitle()
                )));
    }

    @PatchMapping("/chapters/{chapterId}/sections/{sectionId}")
    public ResponseEntity<ApiResponse<SectionResponse>> updateSection(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @PathVariable UUID sectionId,
            @RequestBody @Valid TitleRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(courseContentService.updateSection(
                instructorId, courseId, chapterId, sectionId, request.getTitle()
        )));
    }

    @DeleteMapping("/chapters/{chapterId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @PathVariable UUID sectionId
    ) {
        courseContentService.deleteSection(instructorId, courseId, chapterId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sections/{sectionId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @RequestBody @Valid LessonRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseContentService.createLesson(instructorId, courseId, sectionId, request)));
    }

    @PatchMapping("/sections/{sectionId}/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @PathVariable UUID lessonId,
            @RequestBody @Valid LessonRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                courseContentService.updateLesson(instructorId, courseId, sectionId, lessonId, request)
        ));
    }

    @DeleteMapping("/sections/{sectionId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
            @RequestHeader("X-Instructor-Id") UUID instructorId,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @PathVariable UUID lessonId
    ) {
        courseContentService.deleteLesson(instructorId, courseId, sectionId, lessonId);
        return ResponseEntity.noContent().build();
    }
}
