package edu.ptithcm.learnnextbackend.modules.teacher;

import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    boolean existsByEmail(String email);

    Optional<Teacher> findByEmail(String email);
}
