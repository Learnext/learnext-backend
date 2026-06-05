package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.modules.course.dto.request.CourseSearchRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    List<CourseResponse> searchCourses(CourseSearchRequest request);

    CourseResponse getPublishedCourse(UUID id);
}
