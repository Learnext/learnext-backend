package edu.ptithcm.learnnextbackend.modules.course.dto.response;

import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateCourseResponse {

    private UUID id;
    private String title;
    private String slug;
    private CourseStatus status;
}
