package edu.ptithcm.learnnextbackend.modules.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 5000)
    private String content;
}
