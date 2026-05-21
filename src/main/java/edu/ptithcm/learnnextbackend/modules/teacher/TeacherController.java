package edu.ptithcm.learnnextbackend.modules.teacher;

import edu.ptithcm.learnnextbackend.modules.teacher.dto.request.CreateTeacherRequest;
import edu.ptithcm.learnnextbackend.modules.teacher.dto.response.CreateTeacherResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    @Autowired
    private  TeacherService teacherService;


    @PostMapping("/new")
    public ResponseEntity<CreateTeacherResponse> createTeacher(
            @RequestBody @Valid CreateTeacherRequest createTeacherRequest
    ) {
        CreateTeacherResponse result = teacherService.createTeacher(createTeacherRequest);
        // Add more teachers if needed
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
