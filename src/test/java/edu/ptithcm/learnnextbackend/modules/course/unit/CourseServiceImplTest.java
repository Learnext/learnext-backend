package edu.ptithcm.learnnextbackend.modules.course.unit;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.dto.request.CourseSearchRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.course.impl.CourseServiceImpl;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
    @Mock
    private CourseRepository courseRepository;

    private CourseServiceImpl courseService;
    private Course publishedCourse;

    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(courseRepository);

        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Backend")
                .build();

        Teacher teacher = Teacher.builder()
                .id(UUID.randomUUID())
                .email("teacher@example.com")
                .fullName("Teacher One")
                .status(TeacherStatus.ACTIVE)
                .build();

        publishedCourse = Course.builder()
                .id(UUID.randomUUID())
                .title("Spring Boot Mastery")
                .description("Build production APIs")
                .price(BigDecimal.valueOf(199))
                .thumbnailUrl("thumb.png")
                .previewVideoUrl("preview.mp4")
                .hasPreview(true)
                .rating(BigDecimal.valueOf(4.8))
                .status(CourseStatus.PUBLISHED)
                .category(category)
                .instructor(teacher)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void searchCourses_shouldReturnMappedPublishedCourses() {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setQ("spring");
        request.setSort("rating");

        when(courseRepository.findAll(any(Specification.class), eq(Sort.by(Sort.Direction.DESC, "rating"))))
                .thenReturn(List.of(publishedCourse));

        List<CourseResponse> responses = courseService.searchCourses(request);

        assertEquals(1, responses.size());
        assertEquals("Spring Boot Mastery", responses.get(0).getTitle());
        assertEquals("Backend", responses.get(0).getCategoryName());
        assertEquals("Teacher One", responses.get(0).getInstructorName());
        assertTrue(responses.get(0).isHasPreview());
        verify(courseRepository).findAll(any(Specification.class), eq(Sort.by(Sort.Direction.DESC, "rating")));
    }

    @Test
    void searchCourses_shouldRejectInvalidPriceRange() {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setMinPrice(BigDecimal.valueOf(500));
        request.setMaxPrice(BigDecimal.valueOf(100));

        assertThrows(BadRequestException.class, () -> courseService.searchCourses(request));
    }

    @Test
    void getPublishedCourse_shouldReturnCourse() {
        when(courseRepository.findByIdAndStatus(publishedCourse.getId(), CourseStatus.PUBLISHED))
                .thenReturn(Optional.of(publishedCourse));

        CourseResponse response = courseService.getPublishedCourse(publishedCourse.getId());

        assertEquals(publishedCourse.getId(), response.getId());
        verify(courseRepository).findByIdAndStatus(publishedCourse.getId(), CourseStatus.PUBLISHED);
    }

    @Test
    void getPublishedCourse_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(courseRepository.findByIdAndStatus(id, CourseStatus.PUBLISHED)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> courseService.getPublishedCourse(id));
    }
}
