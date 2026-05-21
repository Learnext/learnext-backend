package edu.ptithcm.learnnextbackend.modules.teacher;

import edu.ptithcm.learnnextbackend.modules.teacher.dto.request.CreateTeacherRequest;
import edu.ptithcm.learnnextbackend.modules.teacher.dto.response.CreateTeacherResponse;

public interface TeacherService {
    CreateTeacherResponse createTeacher(CreateTeacherRequest createTeacherRequest);
}
