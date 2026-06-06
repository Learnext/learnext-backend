package edu.ptithcm.learnnextbackend.modules.learning;

import edu.ptithcm.learnnextbackend.modules.learning.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

    List<Section> findByChapterIdOrderByPositionAsc(UUID chapterId);

}