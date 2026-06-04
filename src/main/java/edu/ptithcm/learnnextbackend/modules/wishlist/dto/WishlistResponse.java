package edu.ptithcm.learnnextbackend.modules.wishlist.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WishlistResponse {

    private UUID id;
    private UUID courseId;
    private String courseTitle;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

}
