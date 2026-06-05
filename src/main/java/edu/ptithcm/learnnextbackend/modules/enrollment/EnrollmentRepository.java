package edu.ptithcm.learnnextbackend.modules.enrollment;

import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.enrollment.entity.Enrollment;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    boolean existsByUserAndCourse(User user, Course course);
}
