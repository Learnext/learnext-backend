package edu.ptithcm.learnnextbackend.modules.course.content.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TitleRequest {
    @NotBlank(message = "Title is required")
    private String title;
}
