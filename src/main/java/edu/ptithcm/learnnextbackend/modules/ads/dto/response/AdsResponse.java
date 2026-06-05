package edu.ptithcm.learnnextbackend.modules.ads.dto.response;

import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdsResponse {

    private UUID id;
    private String title;
    private String imageUrl;
    private String targetUrl;
    private AdsPlacement placement;
    private AdsStatus status;
    private Integer priority;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
