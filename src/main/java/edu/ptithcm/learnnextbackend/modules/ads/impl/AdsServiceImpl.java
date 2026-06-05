package edu.ptithcm.learnnextbackend.modules.ads.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.ads.AdsRepository;
import edu.ptithcm.learnnextbackend.modules.ads.AdsService;
import edu.ptithcm.learnnextbackend.modules.ads.dto.request.CreateAdsRequest;
import edu.ptithcm.learnnextbackend.modules.ads.dto.request.UpdateAdsRequest;
import edu.ptithcm.learnnextbackend.modules.ads.dto.response.AdsResponse;
import edu.ptithcm.learnnextbackend.modules.ads.entity.Ads;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdsServiceImpl implements AdsService {

    @Autowired
    private AdsRepository adsRepository;

    @Override
    public AdsResponse create(CreateAdsRequest request) {
        Ads ads = Ads.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .targetUrl(request.getTargetUrl())
                .placement(request.getPlacement())
                .status(AdsStatus.ACTIVE)
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .build();

        return toResponse(adsRepository.save(ads));
    }

    @Override
    public AdsResponse update(UUID id, UpdateAdsRequest request) {
        Ads ads = adsRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Ads not found"));

        if (request.getTitle() != null) ads.setTitle(request.getTitle());
        if (request.getImageUrl() != null) ads.setImageUrl(request.getImageUrl());
        if (request.getTargetUrl() != null) ads.setTargetUrl(request.getTargetUrl());
        if (request.getPlacement() != null) ads.setPlacement(request.getPlacement());
        if (request.getStatus() != null) ads.setStatus(request.getStatus());
        if (request.getPriority() != null) ads.setPriority(request.getPriority());
        if (request.getStartAt() != null) ads.setStartAt(request.getStartAt());
        if (request.getEndAt() != null) ads.setEndAt(request.getEndAt());

        return toResponse(adsRepository.save(ads));
    }

    @Override
    public void delete(UUID id) {
        Ads ads = adsRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Ads not found"));

        ads.setStatus(AdsStatus.INACTIVE);
        adsRepository.save(ads);
    }

    @Override
    public List<AdsResponse> getActiveAds(AdsPlacement placement) {
        LocalDateTime now = LocalDateTime.now();

        List<Ads> adsList;

        if (placement != null) {
            adsList = adsRepository.findByStatusAndPlacementOrderByPriorityDesc(
                    AdsStatus.ACTIVE,
                    placement
            );
        } else {
            adsList = adsRepository.findByStatusOrderByPriorityDesc(AdsStatus.ACTIVE);
        }

        return adsList.stream()
                .filter(ads -> ads.getStartAt() == null || !ads.getStartAt().isAfter(now))
                .filter(ads -> ads.getEndAt() == null || !ads.getEndAt().isBefore(now))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AdsResponse> getAll() {
        return adsRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AdsResponse toResponse(Ads ads) {
        return AdsResponse.builder()
                .id(ads.getId())
                .title(ads.getTitle())
                .imageUrl(ads.getImageUrl())
                .targetUrl(ads.getTargetUrl())
                .placement(ads.getPlacement())
                .status(ads.getStatus())
                .priority(ads.getPriority())
                .startAt(ads.getStartAt())
                .endAt(ads.getEndAt())
                .build();
    }
}
