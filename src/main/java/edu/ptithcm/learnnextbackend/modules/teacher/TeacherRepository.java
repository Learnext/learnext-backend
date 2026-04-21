package edu.ptithcm.learnnextbackend.modules.teacher;

import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    boolean existsByEmail(String email);
}
