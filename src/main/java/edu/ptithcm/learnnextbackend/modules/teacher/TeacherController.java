package edu.ptithcm.learnnextbackend.modules.teacher;

import edu.ptithcm.learnnextbackend.modules.teacher.dto.request.CreateTeacherRequest;
import edu.ptithcm.learnnextbackend.modules.teacher.dto.response.CreateTeacherResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    @Autowired
    private  TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/new")
    public ResponseEntity<CreateTeacherResponse> createTeacher(
            @RequestBody @Valid CreateTeacherRequest createTeacherRequest
    ) {
        CreateTeacherResponse result = teacherService.createTeacher(createTeacherRequest);
        // Add more teachers if needed
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CreateTeacherResponse>> myTeacher(
            @AuthenticationPrincipal UUID userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Teacher teacher = teacherRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundException("Teacher not found"));
        return ResponseEntity.ok(ApiResponse.success(CreateTeacherResponse.builder()
                .id(teacher.getId().toString())
                .email(teacher.getEmail())
                .fullName(teacher.getFullName())
                .avatarUrl(teacher.getAvatarUrl())
                .bio(teacher.getBio())
                .status(teacher.getStatus())
                .createdAt(teacher.getCreatedAt())
                .build()));
    }
}
