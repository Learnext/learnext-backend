package edu.ptithcm.learnnextbackend.modules.course.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateCourseRequest {

    private String title;

    private String description;

    private String thumbnailUrl;

    private UUID categoryId;

    private BigDecimal price;
}
