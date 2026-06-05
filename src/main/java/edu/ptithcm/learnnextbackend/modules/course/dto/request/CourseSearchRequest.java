package edu.ptithcm.learnnextbackend.modules.course.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CourseSearchRequest {
    private String q;
    private UUID categoryId;
    private UUID instructorId;

    @DecimalMin(value = "0.0", inclusive = true, message = "minPrice must be greater than or equal to 0")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "maxPrice must be greater than or equal to 0")
    private BigDecimal maxPrice;

    private Boolean hasPreview;

    @Pattern(
            regexp = "relevance|newest|priceAsc|priceDesc|rating",
            message = "sort must be one of: relevance, newest, priceAsc, priceDesc, rating"
    )
    private String sort = "relevance";
}
