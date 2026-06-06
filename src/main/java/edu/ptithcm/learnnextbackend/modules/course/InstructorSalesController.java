package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.order.mapper.OrderMapper;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructor/sales")
public class InstructorSalesController {
    private final OrderRepository orderRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TeacherRepository teacherRepository;

    public InstructorSalesController(
            OrderRepository orderRepository,
            EnrollmentRepository enrollmentRepository,
            TeacherRepository teacherRepository
    ) {
        this.orderRepository = orderRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSales(
            @RequestHeader("X-Instructor-Id") UUID instructorId
    ) {
        if (!teacherRepository.existsById(instructorId)) {
            throw new NotFoundException("Instructor not found");
        }

        List<OrderResponse> orders = orderRepository
                .findByCourseInstructorIdOrderByCreatedAtDesc(instructorId)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();

        long studentCount = enrollmentRepository.countByCourseInstructorId(instructorId);
        BigDecimal revenue = orderRepository.sumPaidAmountByInstructorId(instructorId, OrderStatus.PAID);

        Map<String, Object> result = new HashMap<>();
        result.put("orders", orders);
        result.put("totalStudents", studentCount);
        result.put("totalRevenue", revenue != null ? revenue : BigDecimal.ZERO);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
