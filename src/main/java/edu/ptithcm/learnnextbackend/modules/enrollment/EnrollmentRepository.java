package edu.ptithcm.learnnextbackend.modules.enrollment;

import edu.ptithcm.learnnextbackend.modules.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);

    Optional<Enrollment> findByUserIdAndCourseId(UUID userId, UUID courseId);

    List<Enrollment> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByCourseId(UUID courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.id = :instructorId")
    long countByCourseInstructorId(@Param("instructorId") UUID instructorId);
}
