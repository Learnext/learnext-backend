package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.modules.course.dto.request.CreateCourseRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.request.UpdateCourseRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseDetailResponse;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseListResponse;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CreateCourseResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface CourseService {

    CreateCourseResponse create(CreateCourseRequest request);

    CourseDetailResponse getById(UUID id);


}
