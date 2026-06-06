package edu.ptithcm.learnnextbackend.modules.course.content;

import edu.ptithcm.learnnextbackend.modules.course.content.dto.ChapterResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.LessonRequest;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.LessonResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.SectionResponse;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LearningLessonResponse;

import java.util.List;
import java.util.UUID;

public interface CourseContentService {
    List<ChapterResponse> getInstructorContent(UUID instructorId, UUID courseId);

    ChapterResponse createChapter(UUID instructorId, UUID courseId, String title);

    ChapterResponse updateChapter(UUID instructorId, UUID courseId, UUID chapterId, String title);

    void deleteChapter(UUID instructorId, UUID courseId, UUID chapterId);

    SectionResponse createSection(UUID instructorId, UUID courseId, UUID chapterId, String title);

    SectionResponse updateSection(UUID instructorId, UUID courseId, UUID chapterId, UUID sectionId, String title);

    void deleteSection(UUID instructorId, UUID courseId, UUID chapterId, UUID sectionId);

    LessonResponse createLesson(UUID instructorId, UUID courseId, UUID sectionId, LessonRequest request);

    LessonResponse updateLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId, LessonRequest request);

    void deleteLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId);

    List<LearningLessonResponse> getLearningLessons(UUID enrollmentId, UUID courseId);
}
