package edu.ptithcm.learnnextbackend.modules.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private String thumbnailUrl;
    private String previewVideoUrl;
    private boolean hasPreview;
    private BigDecimal rating;
    private String status;
    private UUID categoryId;
    private String categoryName;
    private UUID instructorId;
    private String instructorName;
    private LocalDateTime createdAt;
}
