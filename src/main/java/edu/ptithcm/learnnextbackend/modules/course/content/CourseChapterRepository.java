package edu.ptithcm.learnnextbackend.modules.course.content;

import edu.ptithcm.learnnextbackend.modules.course.content.entity.CourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, UUID> {
    List<CourseChapter> findByCourseIdOrderBySortOrderAscCreatedAtAsc(UUID courseId);

    Optional<CourseChapter> findByIdAndCourseId(UUID id, UUID courseId);

    int countByCourseId(UUID courseId);
}
