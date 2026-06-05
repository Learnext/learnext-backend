package edu.ptithcm.learnnextbackend.modules.learning.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompleteLessonRequest {
    @NotNull(message = "Lesson id is required")
    private UUID lessonId;
}
