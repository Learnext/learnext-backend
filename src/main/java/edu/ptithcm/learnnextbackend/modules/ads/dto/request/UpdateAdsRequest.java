package edu.ptithcm.learnnextbackend.modules.ads.dto.request;

import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateAdsRequest {
    private String title;
    private String imageUrl;
    private String targetUrl;
    private AdsPlacement placement;
    private AdsStatus status;
    private Integer priority;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
