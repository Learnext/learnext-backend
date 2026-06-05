package edu.ptithcm.learnnextbackend.modules.review;

import edu.ptithcm.learnnextbackend.modules.review.dto.request.ReviewRequest;
import edu.ptithcm.learnnextbackend.modules.review.dto.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createOrUpdate(UUID userId, UUID courseId, ReviewRequest request);

    List<ReviewResponse> list(UUID courseId);
}
