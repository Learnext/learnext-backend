package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor {

    boolean existsBySlug(String slug);

    Optional<Course> findBySlug(String slug);

    List<Course> findByTeacherAndStatusNotOrderByCreatedDesc(Teacher teacher, CourseStatus status);

    List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status);
}
