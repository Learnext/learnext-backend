package edu.ptithcm.learnnextbackend.modules.course.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CourseListResponse {

    private UUID id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private BigDecimal price;
    private String status;
    private UUID categoryId;
    private String categoryName;
    private UUID teacherId;
    private String teacherName;
}
