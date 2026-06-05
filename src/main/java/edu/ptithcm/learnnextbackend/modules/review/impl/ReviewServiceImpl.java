package edu.ptithcm.learnnextbackend.modules.review.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.review.ReviewRepository;
import edu.ptithcm.learnnextbackend.modules.review.ReviewService;
import edu.ptithcm.learnnextbackend.modules.review.dto.request.ReviewRequest;
import edu.ptithcm.learnnextbackend.modules.review.dto.response.ReviewResponse;
import edu.ptithcm.learnnextbackend.modules.review.entity.Review;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional
    public ReviewResponse createOrUpdate(UUID userId, UUID courseId, ReviewRequest request) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new NotFoundException("Enrollment not found");
        }

        Review review = reviewRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> Review.builder()
                        .course(courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found")))
                        .user(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")))
                        .build());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        return toResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> list(UUID courseId) {
        return reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
