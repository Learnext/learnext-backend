package edu.ptithcm.learnnextbackend.modules.learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningLessonResponse {
    private UUID id;
    private String title;
    private String type;
    private String videoUrl;
    private String documentUrl;
    private boolean completed;
    private boolean isCompleted;
}
