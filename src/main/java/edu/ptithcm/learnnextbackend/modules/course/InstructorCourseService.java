package edu.ptithcm.learnnextbackend.modules.course;

import edu.ptithcm.learnnextbackend.modules.course.dto.request.InstructorCourseRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;

import java.util.List;
import java.util.UUID;

public interface InstructorCourseService {
    List<CourseResponse> getOwnCourses(UUID instructorId);

    CourseResponse createCourse(UUID instructorId, InstructorCourseRequest request);

    CourseResponse updateCourse(UUID instructorId, UUID courseId, InstructorCourseRequest request);

    CourseResponse publishCourse(UUID instructorId, UUID courseId);

    void archiveCourse(UUID instructorId, UUID courseId);
}
