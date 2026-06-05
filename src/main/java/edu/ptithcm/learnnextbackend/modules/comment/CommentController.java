package edu.ptithcm.learnnextbackend.modules.comment;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.comment.dto.CommentDtos;
import edu.ptithcm.learnnextbackend.modules.comment.entity.CourseComment;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
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

    public CommentController(CommentRepository commentRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDtos.Response>>> list(@PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(commentRepository.findByCourseIdOrderByCreatedAtDesc(courseId)
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
        CourseComment comment = commentRepository.save(CourseComment.builder()
                .course(courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found")))
                .user(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")))
                .content(request.getContent())
                .build());
        return ResponseEntity.ok(ApiResponse.success(toResponse(comment)));
    }

    private CommentDtos.Response toResponse(CourseComment comment) {
        return CommentDtos.Response.builder()
                .id(comment.getId())
                .courseId(comment.getCourse().getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getFullName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
