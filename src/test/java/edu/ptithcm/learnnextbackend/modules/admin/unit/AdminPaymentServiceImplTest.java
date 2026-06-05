package edu.ptithcm.learnnextbackend.modules.admin.unit;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.infrastructure.mail.MailService;
import edu.ptithcm.learnnextbackend.modules.activation.ActivationCodeRepository;
import edu.ptithcm.learnnextbackend.modules.activation.entity.ActivationCode;
import edu.ptithcm.learnnextbackend.modules.admin.dto.response.AdminPaymentResponse;
import edu.ptithcm.learnnextbackend.modules.admin.impl.AdminPaymentServiceImpl;
import edu.ptithcm.learnnextbackend.modules.category.entity.Category;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminPaymentServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ActivationCodeRepository activationCodeRepository;

    @Mock
    private MailService mailService;

    private AdminPaymentServiceImpl service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new AdminPaymentServiceImpl(orderRepository, activationCodeRepository, mailService);
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("learner@example.com")
                .fullName("Learner")
                .status(UserStatus.ACTIVE)
                .build();
        Course course = Course.builder()
                .id(UUID.randomUUID())
                .title("Spring Boot")
                .price(BigDecimal.valueOf(99))
                .category(Category.builder().id(UUID.randomUUID()).name("Backend").build())
                .instructor(Teacher.builder().id(UUID.randomUUID()).fullName("Teacher").build())
                .build();
        order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .status(OrderStatus.PROOF_SUBMITTED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void confirmPayment_shouldCreateActivationCodeAndSendSimulatedEmail() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(activationCodeRepository.existsByOrderId(order.getId())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(activationCodeRepository.save(any(ActivationCode.class))).thenAnswer(invocation -> {
            ActivationCode code = invocation.getArgument(0);
            code.setId(UUID.randomUUID());
            return code;
        });

        AdminPaymentResponse response = service.confirmPayment(order.getId());

        assertEquals(OrderStatus.PAID.name(), response.getOrder().getStatus());
        verify(mailService).sendActivationCode(
                order.getUser().getEmail(),
                order.getUser().getFullName(),
                order.getCourse().getTitle(),
                response.getActivationCode()
        );
    }

    @Test
    void confirmPayment_shouldRejectWrongStatus() {
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> service.confirmPayment(order.getId()));
    }
}
