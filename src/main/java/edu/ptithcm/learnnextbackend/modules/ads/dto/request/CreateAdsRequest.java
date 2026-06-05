package edu.ptithcm.learnnextbackend.modules.ads.dto.request;

import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAdsRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String imageUrl;

    private String targetUrl;

    @NotNull
    private AdsPlacement placement;

    private Integer priority;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
