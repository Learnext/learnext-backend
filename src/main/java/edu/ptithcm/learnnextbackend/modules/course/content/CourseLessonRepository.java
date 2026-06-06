package edu.ptithcm.learnnextbackend.modules.course.content;

import edu.ptithcm.learnnextbackend.modules.course.content.entity.CourseLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson, UUID> {
    List<CourseLesson> findBySectionIdInOrderBySortOrderAscCreatedAtAsc(List<UUID> sectionIds);

    Optional<CourseLesson> findByIdAndSectionId(UUID id, UUID sectionId);

    int countBySectionId(UUID sectionId);
}
