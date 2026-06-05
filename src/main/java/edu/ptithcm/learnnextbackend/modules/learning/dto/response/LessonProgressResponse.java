package edu.ptithcm.learnnextbackend.modules.learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressResponse {
    private UUID id;
    private UUID lessonId;
    private LocalDateTime completedAt;
}
