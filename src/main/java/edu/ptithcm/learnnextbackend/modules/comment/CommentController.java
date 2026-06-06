package edu.ptithcm.learnnextbackend.modules.comment;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.comment.dto.CommentDtos;
import edu.ptithcm.learnnextbackend.modules.comment.entity.CourseComment;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/comments")
public class CommentController {
    private final CommentRepository commentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CommentController(
            CommentRepository commentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.commentRepository = commentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDtos.Response>>> list(@PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(commentRepository.findByCourseIdAndParentIsNullOrderByCreatedAtDesc(courseId)
                .stream()
                .map(this::toResponse)
                .toList()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDtos.Response>> create(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID courseId,
            @RequestBody @Valid CommentDtos.Request request
    ) {
        var course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        // Cho phep: hoc vien da enroll HOAC chinh giang vien cua khoa hoc (de trao doi 2 chieu)
        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        boolean isCourseInstructor = course.getInstructor() != null
                && course.getInstructor().getEmail() != null
                && course.getInstructor().getEmail().equalsIgnoreCase(user.getEmail());
        if (!enrolled && !isCourseInstructor) {
            throw new BadRequestException("Only enrolled users or the course instructor can comment on this course");
        }

        CourseComment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found"));
            if (!parent.getCourse().getId().equals(courseId)) {
                throw new BadRequestException("Parent comment does not belong to this course");
            }
        }

        CourseComment comment = commentRepository.save(CourseComment.builder()
                .course(course)
                .user(user)
                .parent(parent)
                .content(request.getContent())
                .build());
        return ResponseEntity.ok(ApiResponse.success(toResponse(comment)));
    }

    private CommentDtos.Response toResponse(CourseComment comment) {
        return CommentDtos.Response.builder()
                .id(comment.getId())
                .courseId(comment.getCourse().getId())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getFullName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .replies(commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId())
                        .stream()
                        .map(this::toResponse)
                        .toList())
                .build();
    }
}
