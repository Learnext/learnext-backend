package edu.ptithcm.learnnextbackend.modules.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

public final class CommentDtos {
    private CommentDtos() {
    }

    @Getter
    @Setter
    public static class Request {
        @NotBlank
        @Size(max = 5000)
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private UUID courseId;
        private UUID userId;
        private String userName;
        private String content;
        private LocalDateTime createdAt;
    }
}
