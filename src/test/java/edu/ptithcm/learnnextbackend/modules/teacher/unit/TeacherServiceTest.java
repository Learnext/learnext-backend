package edu.ptithcm.learnnextbackend.modules.teacher.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;
import edu.ptithcm.learnnextbackend.common.utils.PasswordUtil; 
import org.mockito.MockedStatic;

import edu.ptithcm.learnnextbackend.modules.teacher.TeacherRepository;
import edu.ptithcm.learnnextbackend.modules.teacher.entity.Teacher;
import edu.ptithcm.learnnextbackend.modules.teacher.enums.TeacherStatus;
import edu.ptithcm.learnnextbackend.modules.teacher.dto.request.CreateTeacherRequest;
import edu.ptithcm.learnnextbackend.modules.teacher.dto.response.CreateTeacherResponse;
import edu.ptithcm.learnnextbackend.modules.teacher.impl.TeacherServiceImpl;

import java.time.LocalDateTime; 
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    //SUCCESS CASE
    @Test 
    void create_teacher_success() { 
        CreateTeacherRequest req = CreateTeacherRequest.builder() 
            .email("tanwibu@gmail.com") 
            .password("12345678") 
            .fullName("Do Thanh Tan") 
            .avatarUrl("avatar.png") 
            .bio("Thanhtanyeuwaifu") 
            .build(); 
        when(teacherRepository.existsByEmail(req.getEmail())).thenReturn(false); 
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) { 
            mocked.when(() -> PasswordUtil.hash(any())) .thenReturn("hashed_password");

            when(teacherRepository.save(any(Teacher.class))) .thenAnswer(inv -> { 
                Teacher t = inv.getArgument(0); 
                t.setId(UUID.randomUUID()); 
                t.setStatus(TeacherStatus.ACTIVE); 
                t.setCreatedAt(LocalDateTime.now());
                 return t; 
            }); 
            CreateTeacherResponse res = teacherService.createTeacher(req); 
            assertNotNull(res); assertEquals(req.getEmail(), res.getEmail()); 
            assertEquals(req.getFullName(), res.getFullName()); 
            assertEquals(TeacherStatus.ACTIVE, res.getStatus()); 
            
            verify(teacherRepository, times(1)).existsByEmail(req.getEmail()); 
            verify(teacherRepository, times(1)).save(any(Teacher.class)); } }

    //EMAIL EXISTS
    @Test
    void create_teacher_email_exists() {
        CreateTeacherRequest req = CreateTeacherRequest.builder()
                .email("tanwibu@gmail.com")
                .password("12345678")
                .fullName("Tan")
                .build();

        when(teacherRepository.existsByEmail(req.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teacherService.createTeacher(req));

        assertEquals("Email already exists", ex.getMessage());

        //not save
        verify(teacherRepository, never()).save(any());
    }

    //VERIFY CALL
    @Test
    void create_teacher_should_call_repository_once() {
        CreateTeacherRequest req = CreateTeacherRequest.builder()
                .email("teacher@gmail.com")
                .password("12345678")
                .fullName("A")
                .build();

        when(teacherRepository.existsByEmail(any())).thenReturn(false);
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) { 
            mocked.when(() -> PasswordUtil.hash(any())) .thenReturn("hashed_password"); 
            when(teacherRepository.save(any())) .thenAnswer(inv -> { 
                Teacher t = inv.getArgument(0); 
                t.setId(UUID.randomUUID()); 
                t.setCreatedAt(LocalDateTime.now()); 
                return t; 
            });

            teacherService.createTeacher(req);

            verify(teacherRepository, times(1)).existsByEmail(req.getEmail()); 
            verify(teacherRepository, times(1)).save(any(Teacher.class));
        }
    }
}

