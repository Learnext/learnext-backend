package edu.ptithcm.learnnextbackend.modules.course.integration;

import edu.ptithcm.learnnextbackend.config.TestJacksonConfig;
import edu.ptithcm.learnnextbackend.config.TestRedisConfig;
import edu.ptithcm.learnnextbackend.modules.category.CategoryRepository;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestRedisConfig.class, TestJacksonConfig.class})
@Transactional
class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Category category;
    private Teacher teacher;

    // =========================
    // SETUP
    // =========================
    @BeforeEach
    void setup() {
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        categoryRepository.deleteAll();

        category = categoryRepository.save(Category.builder()
                .name("Backend")
                .build());

        teacher = teacherRepository.save(Teacher.builder()
                .email("teacher" + UUID.randomUUID() + "@gmail.com")
                .passwordHash("encoded-password")
                .fullName("Teacher Name")
                .status(TeacherStatus.ACTIVE)
                .build());
    }

    // =========================
    // HELPER
    // =========================

    private Course createCourse(CourseStatus status) {
        return courseRepository.save(Course.builder()
                .title("Spring Boot Mastery")
                .description("Build APIs")
                .price(BigDecimal.valueOf(199))
                .thumbnailUrl("thumb.png")
                .previewVideoUrl("preview.mp4")
                .hasPreview(true)
                .rating(BigDecimal.ZERO)
                .status(status)
                .category(category)
                .instructor(teacher)
                .build());
    }

    // =========================
    // TEST: SEARCH COURSES
    // =========================

    @Test
    void searchCourses_should_return_success() throws Exception {

        createCourse(CourseStatus.PUBLISHED);

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // =========================
    // TEST: GET COURSE BY ID
    // =========================

    @Test
    void getCourse_should_return_success() throws Exception {

        Course course = createCourse(CourseStatus.PUBLISHED);

        mockMvc.perform(get("/api/v1/courses/{id}", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(course.getId().toString()))
                .andExpect(jsonPath("$.data.title").value("Spring Boot Mastery"));
    }

    @Test
    void getCourse_should_fail_when_not_published() throws Exception {

        Course course = createCourse(CourseStatus.DRAFT);

        mockMvc.perform(get("/api/v1/courses/{id}", course.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // =========================
    // TEST: FILTER
    // =========================

    @Test
    void searchCourses_should_filter_by_keyword() throws Exception {

        createCourse(CourseStatus.PUBLISHED);

        mockMvc.perform(get("/api/v1/courses")
                        .param("q", "spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}