package edu.ptithcm.learnnextbackend.modules.course.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.category.CategoryRepository;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.InstructorCourseService;
import edu.ptithcm.learnnextbackend.modules.course.dto.request.InstructorCourseRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.course.mapper.CourseMapper;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class InstructorCourseServiceImpl implements InstructorCourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final TeacherRepository teacherRepository;

    public InstructorCourseServiceImpl(
            CourseRepository courseRepository,
            CategoryRepository categoryRepository,
            TeacherRepository teacherRepository
    ) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public List<CourseResponse> getOwnCourses(UUID instructorId) {
        requireInstructor(instructorId);
        return courseRepository.findByInstructorIdAndStatusNotOrderByCreatedAtDesc(
                        instructorId,
                        CourseStatus.ARCHIVED
                )
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponse createCourse(UUID instructorId, InstructorCourseRequest request) {
        Teacher instructor = requireInstructor(instructorId);
        Category category = findOrCreateCategory(request.getCategory());

        Course course = Course.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .price(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice())
                .thumbnailUrl(request.getThumbnailUrl())
                .previewVideoUrl(request.getPreviewVideoUrl())
                .hasPreview(request.getPreviewVideoUrl() != null && !request.getPreviewVideoUrl().isBlank())
                .rating(BigDecimal.ZERO)
                .status(CourseStatus.DRAFT)
                .category(category)
                .instructor(instructor)
                .build();

        return CourseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(UUID instructorId, UUID courseId, InstructorCourseRequest request) {
        Course course = requireOwnedCourse(instructorId, courseId);
        Category category = findOrCreateCategory(request.getCategory());

        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());
        course.setCategory(category);
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPreviewVideoUrl(request.getPreviewVideoUrl());
        course.setHasPreview(request.getPreviewVideoUrl() != null && !request.getPreviewVideoUrl().isBlank());

        return CourseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponse publishCourse(UUID instructorId, UUID courseId) {
        Course course = requireOwnedCourse(instructorId, courseId);
        course.setStatus(CourseStatus.PUBLISHED);
        return CourseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void archiveCourse(UUID instructorId, UUID courseId) {
        Course course = requireOwnedCourse(instructorId, courseId);
        course.setStatus(CourseStatus.ARCHIVED);
        courseRepository.save(course);
    }

    private Teacher requireInstructor(UUID instructorId) {
        return teacherRepository.findById(instructorId)
                .orElseThrow(() -> new NotFoundException("Instructor not found"));
    }

    private Course requireOwnedCourse(UUID instructorId, UUID courseId) {
        return courseRepository.findByIdAndInstructorId(courseId, instructorId)
                .filter(course -> course.getStatus() != CourseStatus.ARCHIVED)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private Category findOrCreateCategory(String name) {
        String normalizedName = name.trim();
        return categoryRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(normalizedName)
                        .build()));
    }
}
