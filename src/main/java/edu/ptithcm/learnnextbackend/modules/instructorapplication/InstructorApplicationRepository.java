package edu.ptithcm.learnnextbackend.modules.instructorapplication;

import edu.ptithcm.learnnextbackend.modules.instructorapplication.entity.InstructorApplication;
import edu.ptithcm.learnnextbackend.modules.instructorapplication.enums.InstructorApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorApplicationRepository extends JpaRepository<InstructorApplication, UUID> {
    boolean existsByUserIdAndStatus(UUID userId, InstructorApplicationStatus status);

    Optional<InstructorApplication> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    List<InstructorApplication> findAllByOrderByCreatedAtDesc();
}
