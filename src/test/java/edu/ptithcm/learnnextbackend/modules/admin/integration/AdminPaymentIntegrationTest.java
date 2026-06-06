package edu.ptithcm.learnnextbackend.modules.admin.integration;

import edu.ptithcm.learnnextbackend.config.TestJacksonConfig;
import edu.ptithcm.learnnextbackend.config.TestRedisConfig;
import edu.ptithcm.learnnextbackend.modules.activation.ActivationCodeRepository;
import edu.ptithcm.learnnextbackend.modules.category.CategoryRepository;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({ TestRedisConfig.class, TestJacksonConfig.class })
@Transactional
class AdminPaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ActivationCodeRepository activationCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Value("${admin.key:dev-admin-key}")
    private String adminKey;

    @BeforeEach
    void setup() {
        activationCodeRepository.deleteAll();
        orderRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    // =========================
    // HELPER
    // =========================

    private Order createOrder(OrderStatus status) {

        User user = userRepository.save(User.builder()
                .email("test" + UUID.randomUUID() + "@gmail.com")
                .fullName("Test User")
                .passwordHash("encoded-password")
                .status(UserStatus.ACTIVE)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("Backend")
                .build());

        Teacher teacher = teacherRepository.save(Teacher.builder()
                .email("teacher" + UUID.randomUUID() + "@gmail.com")
                .passwordHash("encoded-password")
                .fullName("Teacher Name")
                .status(TeacherStatus.ACTIVE)
                .build());

        Course course = courseRepository.save(Course.builder()
                .title("Spring Boot")
                .price(BigDecimal.valueOf(100))
                .hasPreview(false)
                .category(category)
                .instructor(teacher)
                .status(CourseStatus.PUBLISHED)
                .rating(BigDecimal.ZERO)
                .build());

        return orderRepository.save(Order.builder()
                .user(user)
                .course(course)
                .amount(BigDecimal.valueOf(100))
                .status(status)
                .build());
    }

    // =========================
    // GET ORDERS
    // =========================

    @Test
    void getOrders_should_success() throws Exception {

        mockMvc.perform(get("/api/v1/admin/orders")
                .header("X-Admin-Key", adminKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getOrders_should_fail_when_invalid_key() throws Exception {

        mockMvc.perform(get("/api/v1/admin/orders")
                .header("X-Admin-Key", "wrong-key"))
                .andExpect(status().isBadRequest());
    }

    // =========================
    // CONFIRM PAYMENT
    // =========================

    @Test
    void confirmPayment_should_success() throws Exception {

        Order order = createOrder(OrderStatus.PROOF_SUBMITTED);

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/confirm", order.getId())
                .header("X-Admin-Key", adminKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.order.status").value("PAID"))
                .andExpect(jsonPath("$.data.activationCode").exists());

        Order updated = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(activationCodeRepository.count()).isEqualTo(1);
    }

    @Test
    void confirmPayment_should_fail_when_wrong_status() throws Exception {

        Order order = createOrder(OrderStatus.PENDING_PAYMENT);

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/confirm", order.getId())
                .header("X-Admin-Key", adminKey))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmPayment_should_fail_when_invalid_key() throws Exception {

        Order order = createOrder(OrderStatus.PROOF_SUBMITTED);

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/confirm", order.getId())
                .header("X-Admin-Key", "wrong-key"))
                .andExpect(status().isBadRequest());
    }

    // =========================
    // REJECT PAYMENT
    // =========================

    @Test
    void rejectPayment_should_success() throws Exception {

        Order order = createOrder(OrderStatus.PROOF_SUBMITTED);

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/reject", order.getId())
                .header("X-Admin-Key", adminKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REJECTED"));

        Order updated = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.REJECTED);
    }

    @Test
    void rejectPayment_should_fail_when_wrong_status() throws Exception {

        Order order = createOrder(OrderStatus.PAID);

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/reject", order.getId())
                .header("X-Admin-Key", adminKey))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectPayment_should_fail_when_invalid_key() throws Exception {

        Order order = createOrder(OrderStatus.PROOF_SUBMITTED);

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/reject", order.getId())
                .header("X-Admin-Key", "wrong-key"))
                .andExpect(status().isBadRequest());
    }
}