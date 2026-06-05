package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByIdAndStatus(UUID id, CourseStatus status);

    List<Course> findByInstructorIdAndStatusNotOrderByCreatedAtDesc(UUID instructorId, CourseStatus status);

    Optional<Course> findByIdAndInstructorId(UUID id, UUID instructorId);
}
