package edu.ptithcm.learnnextbackend.modules.teacher.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.utils.PasswordUtil;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.TeacherService;
import edu.ptithcm.learnnextbackend.modules.teacher.dto.request.CreateTeacherRequest;
import edu.ptithcm.learnnextbackend.modules.teacher.dto.response.CreateTeacherResponse;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public CreateTeacherResponse createTeacher(CreateTeacherRequest createTeacherRequest) {

        //check teacher exist by email
        if (teacherRepository.existsByEmail(createTeacherRequest.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        String passwordHash = PasswordUtil.hash(createTeacherRequest.getPassword());

        Teacher teacher = Teacher.builder()
                .email(createTeacherRequest.getEmail())
                .passwordHash(passwordHash) // ⚠️ nên encode
                .fullName(createTeacherRequest.getFullName())
                .avatarUrl(createTeacherRequest.getAvatarUrl())
                .bio(createTeacherRequest.getBio())
                .status(TeacherStatus.ACTIVE)
                .build();
        try{
            Teacher saved = teacherRepository.save(teacher);

            CreateTeacherResponse res = CreateTeacherResponse.builder()
                    .id(saved.getId().toString())
                    .email(saved.getEmail())
                    .fullName(saved.getFullName())
                    .avatarUrl(saved.getAvatarUrl())
                    .bio(saved.getBio())
                    .status(saved.getStatus())
                    .createdAt(saved.getCreatedAt())
                    .build();

            return res;
        }catch (RuntimeException exception){
            throw new RuntimeException("Tạo teacher không thành công!");
        }
    }
}
