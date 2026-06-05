package edu.ptithcm.learnnextbackend.modules.ads;

import edu.ptithcm.learnnextbackend.modules.ads.dto.response.AdsResponse;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ads")
public class AdsController {

    @Autowired
    private AdsService adsService;

    @GetMapping("/active")
    public ResponseEntity<List<AdsResponse>> getActiveAds(@RequestParam(required = false) AdsPlacement placement) {
        return ResponseEntity.ok(adsService.getActiveAds(placement));
    }
}
