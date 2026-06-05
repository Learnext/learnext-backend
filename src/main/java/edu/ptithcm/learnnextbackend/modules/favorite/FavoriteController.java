package edu.ptithcm.learnnextbackend.modules.favorite;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.mapper.CourseMapper;
import edu.ptithcm.learnnextbackend.modules.favorite.entity.Favorite;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {
    private final FavoriteRepository favoriteRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteRepository favoriteRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> list(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(favorite -> CourseMapper.toResponse(favorite.getCourse()))
                .toList()));
    }

    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> add(@AuthenticationPrincipal UUID userId, @PathVariable UUID courseId) {
        Favorite favorite = favoriteRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> favoriteRepository.save(Favorite.builder()
                        .user(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")))
                        .course(courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found")))
                        .build()));
        return ResponseEntity.ok(ApiResponse.success(CourseMapper.toResponse(favorite.getCourse())));
    }

    @DeleteMapping("/{courseId}")
    @Transactional
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UUID userId, @PathVariable UUID courseId) {
        favoriteRepository.deleteByUserIdAndCourseId(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}
