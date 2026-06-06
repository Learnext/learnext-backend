package edu.ptithcm.learnnextbackend.modules.course.content;

import edu.ptithcm.learnnextbackend.modules.course.content.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID> {
    List<CourseSection> findByChapterIdInOrderBySortOrderAscCreatedAtAsc(List<UUID> chapterIds);

    Optional<CourseSection> findByIdAndChapterId(UUID id, UUID chapterId);

    int countByChapterId(UUID chapterId);
}
