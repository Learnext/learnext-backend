package edu.ptithcm.learnnextbackend.modules.instructorapplication;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.instructorapplication.dto.InstructorApplicationRequest;
import edu.ptithcm.learnnextbackend.modules.instructorapplication.dto.InstructorApplicationResponse;
import edu.ptithcm.learnnextbackend.modules.instructorapplication.entity.InstructorApplication;
import edu.ptithcm.learnnextbackend.modules.instructorapplication.enums.InstructorApplicationStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructor-applications")
public class InstructorApplicationController {
    private final InstructorApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final String adminKey;

    public InstructorApplicationController(
            InstructorApplicationRepository applicationRepository,
            UserRepository userRepository,
            TeacherRepository teacherRepository,
            @Value("${admin.key:dev-admin-key}") String adminKey
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.adminKey = adminKey;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstructorApplicationResponse>> apply(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid InstructorApplicationRequest request
    ) {
        if (applicationRepository.existsByUserIdAndStatus(userId, InstructorApplicationStatus.PENDING)) {
            throw new BadRequestException("You already have a pending instructor application");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        InstructorApplication application = applicationRepository.save(InstructorApplication.builder()
                .user(user)
                .qualification(request.getQualification().trim())
                .phone(request.getPhone().trim())
                .certificateUrl(request.getCertificateUrl().trim())
                .bio(request.getBio())
                .status(InstructorApplicationStatus.PENDING)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(InstructorApplicationMapper.toResponse(application)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<InstructorApplicationResponse>> myApplication(
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(applicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(InstructorApplicationMapper::toResponse)
                .orElse(null)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<InstructorApplicationResponse>>> allApplications(
            @RequestHeader("X-Admin-Key") String providedKey
    ) {
        requireAdmin(providedKey);
        return ResponseEntity.ok(ApiResponse.success(applicationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(InstructorApplicationMapper::toResponse)
                .toList()));
    }

    @PatchMapping("/admin/{applicationId}/approve")
    public ResponseEntity<ApiResponse<InstructorApplicationResponse>> approve(
            @RequestHeader("X-Admin-Key") String providedKey,
            @PathVariable UUID applicationId
    ) {
        requireAdmin(providedKey);
        InstructorApplication application = requireApplication(applicationId);
        application.setStatus(InstructorApplicationStatus.APPROVED);
        if (!teacherRepository.existsByEmail(application.getUser().getEmail())) {
            teacherRepository.save(Teacher.builder()
                    .email(application.getUser().getEmail())
                    .passwordHash(application.getUser().getPasswordHash())
                    .fullName(application.getUser().getFullName())
                    .bio(application.getBio())
                    .status(TeacherStatus.ACTIVE)
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.success(
                InstructorApplicationMapper.toResponse(applicationRepository.save(application))
        ));
    }

    @PatchMapping("/admin/{applicationId}/reject")
    public ResponseEntity<ApiResponse<InstructorApplicationResponse>> reject(
            @RequestHeader("X-Admin-Key") String providedKey,
            @PathVariable UUID applicationId
    ) {
        requireAdmin(providedKey);
        InstructorApplication application = requireApplication(applicationId);
        application.setStatus(InstructorApplicationStatus.REJECTED);
        return ResponseEntity.ok(ApiResponse.success(
                InstructorApplicationMapper.toResponse(applicationRepository.save(application))
        ));
    }

    private InstructorApplication requireApplication(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    private void requireAdmin(String providedKey) {
        if (!adminKey.equals(providedKey)) {
            throw new BadRequestException("Invalid admin key");
        }
    }
}
