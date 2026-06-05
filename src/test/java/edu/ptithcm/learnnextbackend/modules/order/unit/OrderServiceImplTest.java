package edu.ptithcm.learnnextbackend.modules.order.unit;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.CreateOrderRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.SubmitPaymentProofRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.order.impl.OrderServiceImpl;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    private OrderServiceImpl orderService;
    private User user;
    private Course course;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, userRepository, courseRepository);

        user = User.builder()
                .id(UUID.randomUUID())
                .email("buyer@example.com")
                .fullName("Buyer")
                .status(UserStatus.ACTIVE)
                .build();

        course = Course.builder()
                .id(UUID.randomUUID())
                .title("Spring Boot")
                .price(BigDecimal.valueOf(99))
                .status(CourseStatus.PUBLISHED)
                .category(Category.builder().id(UUID.randomUUID()).name("Backend").build())
                .instructor(Teacher.builder().id(UUID.randomUUID()).fullName("Teacher").build())
                .build();
    }

    @Test
    void createOrder_shouldSnapshotPublishedCoursePrice() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCourseId(course.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(courseRepository.findByIdAndStatus(course.getId(), CourseStatus.PUBLISHED)).thenReturn(Optional.of(course));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        OrderResponse response = orderService.createOrder(user.getId(), request);

        assertEquals(course.getId(), response.getCourseId());
        assertEquals(BigDecimal.valueOf(99), response.getAmount());
        assertEquals(OrderStatus.PENDING_PAYMENT.name(), response.getStatus());
    }

    @Test
    void createOrder_shouldRejectUnpublishedCourse() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCourseId(course.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(courseRepository.findByIdAndStatus(course.getId(), CourseStatus.PUBLISHED)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(user.getId(), request));
    }

    @Test
    void submitPaymentProof_shouldRequireOwnedPendingOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .status(OrderStatus.PENDING_PAYMENT)
                .build();
        SubmitPaymentProofRequest request = new SubmitPaymentProofRequest();
        request.setPaymentProofUrl("https://example.com/proof.png");

        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.submitPaymentProof(user.getId(), orderId, request);

        assertEquals(OrderStatus.PROOF_SUBMITTED.name(), response.getStatus());
        assertEquals("https://example.com/proof.png", response.getPaymentProofUrl());
    }

    @Test
    void submitPaymentProof_shouldRejectPaidOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .status(OrderStatus.PAID)
                .build();
        SubmitPaymentProofRequest request = new SubmitPaymentProofRequest();
        request.setPaymentProofUrl("https://example.com/proof.png");

        when(orderRepository.findByIdAndUserId(orderId, user.getId())).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> orderService.submitPaymentProof(user.getId(), orderId, request));
    }
}
