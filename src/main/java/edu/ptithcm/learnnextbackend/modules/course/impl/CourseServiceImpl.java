package edu.ptithcm.learnnextbackend.modules.course.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.cart.CartRepository;
import edu.ptithcm.learnnextbackend.modules.category.CategoryRepository;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.CourseService;
import edu.ptithcm.learnnextbackend.modules.course.dto.request.CreateCourseRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseDetailResponse;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CreateCourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public CreateCourseResponse create(CreateCourseRequest request) {
        Teacher teacher = getCurrentTeacher();

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BadRequestException("Category not found"));
        }

        String slug = generateUniqueSlug(request.getTitle());

        Course course = Course.builder()
                .title(request.getTitle())
                .slug(slug)
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .teacher(teacher)
                .category(category)
                .price(request.getPrice())
                .status(CourseStatus.DRAFT)
                .build();

        Course savedCourse = courseRepository.save(course);

        return CreateCourseResponse.builder()
                .id(savedCourse.getId())
                .title(savedCourse.getTitle())
                .slug(savedCourse.getSlug())
                .status(savedCourse.getStatus())
                .build();
    }

    @Override
    public CourseDetailResponse getById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Course not found"));

        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new BadRequestException("Course not found");
        }

        return toDetailResponse(course);
    }


    private Teacher getCurrentTeacher() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return teacherRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Teacher profile not found"));
    }

    private void checkOwner(Course course, Teacher teacher) {
        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("You do not have permission to access this course");
        }
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = toSlug(title);
        String slug = baseSlug;
        int count = 1;

        while (courseRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count;
            count++;
        }

        return slug;
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        String slug = normalized
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (slug.isBlank()) {
            throw new BadRequestException("Invalid course title");
        }

        return slug;
    }

    private CourseDetailResponse toDetailResponse(Course course) {
        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .status(course.getStatus())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .publishedAt(course.getPublishedAt())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
