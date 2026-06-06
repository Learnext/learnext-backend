package edu.ptithcm.learnnextbackend.modules.learning.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.enrollment.dto.response.EnrollmentResponse;
import edu.ptithcm.learnnextbackend.modules.enrollment.entity.Enrollment;
import edu.ptithcm.learnnextbackend.modules.enrollment.mapper.EnrollmentMapper;
import edu.ptithcm.learnnextbackend.modules.learning.LearningService;
import edu.ptithcm.learnnextbackend.modules.learning.LessonProgressRepository;
import edu.ptithcm.learnnextbackend.modules.learning.dto.request.CompleteLessonRequest;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LearningAccessResponse;
import edu.ptithcm.learnnextbackend.modules.learning.dto.response.LessonProgressResponse;
import edu.ptithcm.learnnextbackend.modules.learning.entity.Lesson;
import edu.ptithcm.learnnextbackend.modules.learning.entity.LessonProgress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LearningServiceImpl implements LearningService {
        private final EnrollmentRepository enrollmentRepository;
        private final LessonProgressRepository lessonProgressRepository;

        public LearningServiceImpl(
                        EnrollmentRepository enrollmentRepository,
                        LessonProgressRepository lessonProgressRepository) {
                this.enrollmentRepository = enrollmentRepository;
                this.lessonProgressRepository = lessonProgressRepository;
        }

        @Override
        public List<EnrollmentResponse> getEnrollments(UUID userId) {
                return enrollmentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                                .stream()
                                .map(EnrollmentMapper::toResponse)
                                .toList();
        }

        @Override
        public LearningAccessResponse getCourseAccess(UUID userId, UUID courseId) {
                return LearningAccessResponse.builder()
                                .courseId(courseId)
                                .access(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId))
                                .build();
        }

        @Override
        @Transactional
        public LessonProgressResponse completeLesson(
                        UUID userId,
                        UUID courseId,
                        CompleteLessonRequest request) {

                Enrollment enrollment = enrollmentRepository
                                .findByUserIdAndCourseId(userId, courseId)
                                .orElseThrow(() -> new NotFoundException("Enrollment not found"));

                LessonProgress progress = lessonProgressRepository
                                .findByEnrollmentIdAndLessonId(enrollment.getId(), request.getLessonId())
                                .orElseGet(() -> lessonProgressRepository.save(
                                                LessonProgress.builder()
                                                                .enrollment(enrollment)
                                                                .lesson(Lesson.builder()
                                                                                .id(request.getLessonId())
                                                                                .build())
                                                                .build()));

                return LessonProgressResponse.builder()
                                .id(progress.getId())
                                .lessonId(progress.getLesson().getId())
                                .completedAt(progress.getCompletedAt())
                                .build();
        }
}
