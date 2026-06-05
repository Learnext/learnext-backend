package edu.ptithcm.learnnextbackend.modules.review.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private UUID id;
    private UUID courseId;
    private UUID userId;
    private String userName;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
}
