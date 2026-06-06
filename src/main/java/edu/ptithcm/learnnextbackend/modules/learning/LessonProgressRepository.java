package edu.ptithcm.learnnextbackend.modules.learning;

import edu.ptithcm.learnnextbackend.modules.learning.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(UUID enrollmentId, UUID lessonId);

    boolean existsByEnrollmentIdAndLessonId(UUID enrollmentId, UUID lessonId);
}
