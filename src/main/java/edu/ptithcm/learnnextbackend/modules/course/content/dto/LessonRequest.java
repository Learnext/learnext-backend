package edu.ptithcm.learnnextbackend.modules.course.content.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Type is required")
    private String type;

    private String videoUrl;
    private String documentUrl;
    private String file;
}
