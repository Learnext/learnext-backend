package edu.ptithcm.learnnextbackend.modules.course.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InstructorCourseRequest {
    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 20000, message = "Description must be at most 20000 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotBlank(message = "Category must not be blank")
    @Size(max = 120, message = "Category must be at most 120 characters")
    private String category;

    @Size(max = 1000, message = "Thumbnail URL must be at most 1000 characters")
    private String thumbnailUrl;

    @Size(max = 1000, message = "Preview video URL must be at most 1000 characters")
    private String previewVideoUrl;
}
