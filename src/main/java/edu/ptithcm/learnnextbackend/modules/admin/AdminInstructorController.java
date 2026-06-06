package edu.ptithcm.learnnextbackend.modules.admin;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.admin.dto.response.InstructorSummaryResponse;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/instructors")
public class AdminInstructorController {
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;
    private final String adminKey;

    public AdminInstructorController(
            TeacherRepository teacherRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            OrderRepository orderRepository,
            @Value("${admin.key:dev-admin-key}") String adminKey
    ) {
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.orderRepository = orderRepository;
        this.adminKey = adminKey;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<InstructorSummaryResponse>>> getInstructors(
            @RequestHeader("X-Admin-Key") String providedKey
    ) {
        requireAdmin(providedKey);
        List<InstructorSummaryResponse> result = teacherRepository.findAll().stream()
                .map(t -> toSummary(t))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{teacherId}/toggle-status")
    @Transactional
    public ResponseEntity<ApiResponse<InstructorSummaryResponse>> toggleStatus(
            @RequestHeader("X-Admin-Key") String providedKey,
            @PathVariable UUID teacherId
    ) {
        requireAdmin(providedKey);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Instructor not found"));
        teacher.setStatus(teacher.getStatus() == TeacherStatus.ACTIVE ? TeacherStatus.BLOCKED : TeacherStatus.ACTIVE);
        teacherRepository.save(teacher);
        return ResponseEntity.ok(ApiResponse.success(toSummary(teacher)));
    }

    private InstructorSummaryResponse toSummary(Teacher t) {
        return InstructorSummaryResponse.builder()
                .id(t.getId().toString())
                .fullName(t.getFullName())
                .email(t.getEmail())
                .status(t.getStatus().name())
                .courseCount(courseRepository.countByInstructorId(t.getId()))
                .studentCount(enrollmentRepository.countByCourseInstructorId(t.getId()))
                .revenue(orderRepository.sumPaidAmountByInstructorId(t.getId(), OrderStatus.PAID))
                .createdAt(t.getCreatedAt())
                .build();
    }

    private void requireAdmin(String key) {
        if (!adminKey.equals(key)) throw new BadRequestException("Invalid admin key");
    }
}
