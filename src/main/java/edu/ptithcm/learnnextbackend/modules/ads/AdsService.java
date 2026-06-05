package edu.ptithcm.learnnextbackend.modules.ads;

import edu.ptithcm.learnnextbackend.modules.ads.dto.request.CreateAdsRequest;
import edu.ptithcm.learnnextbackend.modules.ads.dto.request.UpdateAdsRequest;
import edu.ptithcm.learnnextbackend.modules.ads.dto.response.AdsResponse;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;

import java.util.List;
import java.util.UUID;

public interface AdsService {

    AdsResponse create(CreateAdsRequest request);

    AdsResponse update(UUID id, UpdateAdsRequest request);

    void delete(UUID id);

    List<AdsResponse> getActiveAds(AdsPlacement placement);

    List<AdsResponse> getAll();
}
