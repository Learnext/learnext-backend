package edu.ptithcm.learnnextbackend.modules.review;

import edu.ptithcm.learnnextbackend.modules.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByCourseIdOrderByCreatedAtDesc(UUID courseId);

    Optional<Review> findByUserIdAndCourseId(UUID userId, UUID courseId);
}
