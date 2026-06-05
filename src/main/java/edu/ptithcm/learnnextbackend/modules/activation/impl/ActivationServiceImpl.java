package edu.ptithcm.learnnextbackend.modules.activation.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.activation.ActivationCodeRepository;
import edu.ptithcm.learnnextbackend.modules.activation.ActivationService;
import edu.ptithcm.learnnextbackend.modules.activation.dto.request.ActivateCourseRequest;
import edu.ptithcm.learnnextbackend.modules.activation.entity.ActivationCode;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.enrollment.dto.response.EnrollmentResponse;
import edu.ptithcm.learnnextbackend.modules.enrollment.entity.Enrollment;
import edu.ptithcm.learnnextbackend.modules.enrollment.mapper.EnrollmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ActivationServiceImpl implements ActivationService {
    private final ActivationCodeRepository activationCodeRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ActivationServiceImpl(
            ActivationCodeRepository activationCodeRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.activationCodeRepository = activationCodeRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional
    public EnrollmentResponse activateCourse(UUID userId, ActivateCourseRequest request) {
        ActivationCode activationCode = activationCodeRepository.findByCode(request.getActivationCode().trim())
                .orElseThrow(() -> new NotFoundException("Activation code not found"));

        if (!activationCode.getUser().getId().equals(userId)) {
            throw new NotFoundException("Activation code not found");
        }
        if (activationCode.getUsedAt() != null) {
            throw new BadRequestException("Activation code has already been used");
        }
        if (activationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Activation code has expired");
        }
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, activationCode.getCourse().getId())) {
            throw new BadRequestException("Course already activated");
        }

        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .user(activationCode.getUser())
                .course(activationCode.getCourse())
                .build());
        activationCode.setUsedAt(LocalDateTime.now());
        activationCodeRepository.save(activationCode);

        return EnrollmentMapper.toResponse(enrollment);
    }
}
