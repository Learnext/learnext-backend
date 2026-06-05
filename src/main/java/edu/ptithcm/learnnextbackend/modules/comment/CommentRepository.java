package edu.ptithcm.learnnextbackend.modules.comment;

import edu.ptithcm.learnnextbackend.modules.comment.entity.CourseComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CourseComment, UUID> {
    List<CourseComment> findByCourseIdOrderByCreatedAtDesc(UUID courseId);
}
