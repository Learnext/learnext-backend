package edu.ptithcm.learnnextbackend.modules.course.content.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.content.CourseChapterRepository;
import edu.ptithcm.learnnextbackend.modules.course.content.CourseContentService;
import edu.ptithcm.learnnextbackend.modules.course.content.CourseLessonRepository;
import edu.ptithcm.learnnextbackend.modules.course.content.CourseSectionRepository;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.ChapterResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.LessonRequest;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.LessonResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.dto.SectionResponse;
import edu.ptithcm.learnnextbackend.modules.course.content.entity.CourseChapter;
import edu.ptithcm.learnnextbackend.modules.course.content.entity.CourseLesson;
import edu.ptithcm.learnnextbackend.modules.course.content.entity.CourseSection;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.learning.LessonProgressRepository;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LearningLessonResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseContentServiceImpl implements CourseContentService {
    private final CourseRepository courseRepository;
    private final CourseChapterRepository chapterRepository;
    private final CourseSectionRepository sectionRepository;
    private final CourseLessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public CourseContentServiceImpl(
            CourseRepository courseRepository,
            CourseChapterRepository chapterRepository,
            CourseSectionRepository sectionRepository,
            CourseLessonRepository lessonRepository,
            LessonProgressRepository lessonProgressRepository
    ) {
        this.courseRepository = courseRepository;
        this.chapterRepository = chapterRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    @Override
    public List<ChapterResponse> getInstructorContent(UUID instructorId, UUID courseId) {
        requireOwnedCourse(instructorId, courseId);
        return getCourseContent(courseId);
    }

    @Override
    @Transactional
    public ChapterResponse createChapter(UUID instructorId, UUID courseId, String title) {
        Course course = requireOwnedCourse(instructorId, courseId);
        CourseChapter chapter = CourseChapter.builder()
                .course(course)
                .title(normalizeTitle(title))
                .sortOrder(chapterRepository.countByCourseId(courseId))
                .build();
        return toChapterResponse(chapterRepository.save(chapter), List.of());
    }

    @Override
    @Transactional
    public ChapterResponse updateChapter(UUID instructorId, UUID courseId, UUID chapterId, String title) {
        requireOwnedCourse(instructorId, courseId);
        CourseChapter chapter = requireChapter(courseId, chapterId);
        chapter.setTitle(normalizeTitle(title));
        return toChapterResponse(chapterRepository.save(chapter), getSectionsForChapter(chapterId));
    }

    @Override
    @Transactional
    public void deleteChapter(UUID instructorId, UUID courseId, UUID chapterId) {
        requireOwnedCourse(instructorId, courseId);
        chapterRepository.delete(requireChapter(courseId, chapterId));
    }

    @Override
    @Transactional
    public SectionResponse createSection(UUID instructorId, UUID courseId, UUID chapterId, String title) {
        requireOwnedCourse(instructorId, courseId);
        CourseChapter chapter = requireChapter(courseId, chapterId);
        CourseSection section = CourseSection.builder()
                .chapter(chapter)
                .title(normalizeTitle(title))
                .sortOrder(sectionRepository.countByChapterId(chapterId))
                .build();
        return toSectionResponse(sectionRepository.save(section), List.of());
    }

    @Override
    @Transactional
    public SectionResponse updateSection(UUID instructorId, UUID courseId, UUID chapterId, UUID sectionId, String title) {
        requireOwnedCourse(instructorId, courseId);
        requireChapter(courseId, chapterId);
        CourseSection section = requireSection(chapterId, sectionId);
        section.setTitle(normalizeTitle(title));
        return toSectionResponse(sectionRepository.save(section), getLessonsForSection(sectionId));
    }

    @Override
    @Transactional
    public void deleteSection(UUID instructorId, UUID courseId, UUID chapterId, UUID sectionId) {
        requireOwnedCourse(instructorId, courseId);
        requireChapter(courseId, chapterId);
        sectionRepository.delete(requireSection(chapterId, sectionId));
    }

    @Override
    @Transactional
    public LessonResponse createLesson(UUID instructorId, UUID courseId, UUID sectionId, LessonRequest request) {
        requireOwnedCourse(instructorId, courseId);
        CourseSection section = requireSectionInCourse(courseId, sectionId);
        CourseLesson lesson = CourseLesson.builder()
                .section(section)
                .title(normalizeTitle(request.getTitle()))
                .type(normalizeType(request.getType()))
                .videoUrl(blankToNull(request.getVideoUrl()))
                .documentUrl(blankToNull(request.getDocumentUrl()))
                .fileName(blankToNull(request.getFile()))
                .sortOrder(lessonRepository.countBySectionId(sectionId))
                .build();
        applyLessonUrlDefaults(lesson);
        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId, LessonRequest request) {
        requireOwnedCourse(instructorId, courseId);
        requireSectionInCourse(courseId, sectionId);
        CourseLesson lesson = requireLesson(sectionId, lessonId);
        lesson.setTitle(normalizeTitle(request.getTitle()));
        lesson.setType(normalizeType(request.getType()));
        lesson.setVideoUrl(blankToNull(request.getVideoUrl()));
        lesson.setDocumentUrl(blankToNull(request.getDocumentUrl()));
        lesson.setFileName(blankToNull(request.getFile()));
        applyLessonUrlDefaults(lesson);
        return toLessonResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId) {
        requireOwnedCourse(instructorId, courseId);
        requireSectionInCourse(courseId, sectionId);
        lessonRepository.delete(requireLesson(sectionId, lessonId));
    }

    @Override
    public List<LearningLessonResponse> getLearningLessons(UUID enrollmentId, UUID courseId) {
        List<CourseChapter> chapters = chapterRepository.findByCourseIdOrderBySortOrderAscCreatedAtAsc(courseId);
        if (chapters.isEmpty()) {
            return List.of();
        }
        List<UUID> chapterIds = chapters.stream().map(CourseChapter::getId).toList();
        List<CourseSection> sections = sectionRepository.findByChapterIdInOrderBySortOrderAscCreatedAtAsc(chapterIds);
        if (sections.isEmpty()) {
            return List.of();
        }
        List<UUID> sectionIds = sections.stream().map(CourseSection::getId).toList();
        return lessonRepository.findBySectionIdInOrderBySortOrderAscCreatedAtAsc(sectionIds)
                .stream()
                .map(lesson -> {
                    boolean completed = lessonProgressRepository.existsByEnrollmentIdAndLessonId(enrollmentId, lesson.getId());
                    return LearningLessonResponse.builder()
                            .id(lesson.getId())
                            .title(lesson.getTitle())
                            .type(lesson.getType())
                            .videoUrl(lesson.getVideoUrl())
                            .documentUrl(lesson.getDocumentUrl())
                            .completed(completed)
                            .isCompleted(completed)
                            .build();
                })
                .toList();
    }

    private List<ChapterResponse> getCourseContent(UUID courseId) {
        List<CourseChapter> chapters = chapterRepository.findByCourseIdOrderBySortOrderAscCreatedAtAsc(courseId);
        if (chapters.isEmpty()) {
            return List.of();
        }
        List<UUID> chapterIds = chapters.stream().map(CourseChapter::getId).toList();
        List<CourseSection> sections = sectionRepository.findByChapterIdInOrderBySortOrderAscCreatedAtAsc(chapterIds);
        List<UUID> sectionIds = sections.stream().map(CourseSection::getId).toList();
        List<CourseLesson> lessons = sectionIds.isEmpty()
                ? List.of()
                : lessonRepository.findBySectionIdInOrderBySortOrderAscCreatedAtAsc(sectionIds);

        Map<UUID, List<CourseLesson>> lessonsBySection = lessons.stream()
                .collect(Collectors.groupingBy(lesson -> lesson.getSection().getId()));
        Map<UUID, List<CourseSection>> sectionsByChapter = sections.stream()
                .collect(Collectors.groupingBy(section -> section.getChapter().getId()));

        return chapters.stream()
                .map(chapter -> toChapterResponse(chapter, sectionsByChapter
                        .getOrDefault(chapter.getId(), List.of())
                        .stream()
                        .map(section -> toSectionResponse(section, lessonsBySection
                                .getOrDefault(section.getId(), List.of())
                                .stream()
                                .map(this::toLessonResponse)
                                .toList()))
                        .toList()))
                .toList();
    }

    private List<SectionResponse> getSectionsForChapter(UUID chapterId) {
        List<CourseSection> sections = sectionRepository.findByChapterIdInOrderBySortOrderAscCreatedAtAsc(List.of(chapterId));
        return sections.stream()
                .map(section -> toSectionResponse(section, getLessonsForSection(section.getId())))
                .toList();
    }

    private List<LessonResponse> getLessonsForSection(UUID sectionId) {
        return lessonRepository.findBySectionIdInOrderBySortOrderAscCreatedAtAsc(List.of(sectionId))
                .stream()
                .map(this::toLessonResponse)
                .toList();
    }

    private Course requireOwnedCourse(UUID instructorId, UUID courseId) {
        return courseRepository.findByIdAndInstructorId(courseId, instructorId)
                .filter(course -> course.getStatus() != CourseStatus.ARCHIVED)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private CourseChapter requireChapter(UUID courseId, UUID chapterId) {
        return chapterRepository.findByIdAndCourseId(chapterId, courseId)
                .orElseThrow(() -> new NotFoundException("Chapter not found"));
    }

    private CourseSection requireSection(UUID chapterId, UUID sectionId) {
        return sectionRepository.findByIdAndChapterId(sectionId, chapterId)
                .orElseThrow(() -> new NotFoundException("Section not found"));
    }

    private CourseSection requireSectionInCourse(UUID courseId, UUID sectionId) {
        return getCourseContent(courseId).stream()
                .flatMap(chapter -> chapter.getSections().stream())
                .filter(section -> section.getId().equals(sectionId))
                .findFirst()
                .map(section -> sectionRepository.findById(section.getId())
                        .orElseThrow(() -> new NotFoundException("Section not found")))
                .orElseThrow(() -> new NotFoundException("Section not found"));
    }

    private CourseLesson requireLesson(UUID sectionId, UUID lessonId) {
        return lessonRepository.findByIdAndSectionId(lessonId, sectionId)
                .orElseThrow(() -> new NotFoundException("Lesson not found"));
    }

    private String normalizeTitle(String title) {
        String normalized = title == null ? "" : title.trim();
        if (normalized.isEmpty()) {
            throw new BadRequestException("Title is required");
        }
        return normalized;
    }

    private String normalizeType(String type) {
        String normalized = type == null ? "" : type.trim().toLowerCase();
        if (!List.of("video", "pdf").contains(normalized)) {
            throw new BadRequestException("Lesson type must be video or pdf");
        }
        return normalized;
    }

    private void applyLessonUrlDefaults(CourseLesson lesson) {
        if ("video".equals(lesson.getType())) {
            lesson.setVideoUrl(firstNonBlank(lesson.getVideoUrl(), lesson.getFileName()));
            lesson.setDocumentUrl(null);
        } else {
            lesson.setDocumentUrl(firstNonBlank(lesson.getDocumentUrl(), lesson.getFileName()));
            lesson.setVideoUrl(null);
        }
    }

    private String firstNonBlank(String first, String second) {
        String normalizedFirst = blankToNull(first);
        return normalizedFirst != null ? normalizedFirst : blankToNull(second);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private ChapterResponse toChapterResponse(CourseChapter chapter, List<SectionResponse> sections) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .sections(sections == null ? new ArrayList<>() : sections)
                .build();
    }

    private SectionResponse toSectionResponse(CourseSection section, List<LessonResponse> lessons) {
        return SectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .lessons(lessons == null ? new ArrayList<>() : lessons)
                .build();
    }

    private LessonResponse toLessonResponse(CourseLesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .type(lesson.getType())
                .videoUrl(lesson.getVideoUrl())
                .documentUrl(lesson.getDocumentUrl())
                .file(lesson.getFileName())
                .build();
    }
}
