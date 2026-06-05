package edu.ptithcm.learnnextbackend.modules.review;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.review.dto.request.ReviewRequest;
import edu.ptithcm.learnnextbackend.modules.review.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> list(@PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.list(courseId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createOrUpdate(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID courseId,
            @RequestBody @Valid ReviewRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.createOrUpdate(userId, courseId, request)));
    }
}
