package edu.ptithcm.learnnextbackend.modules.ads;

import edu.ptithcm.learnnextbackend.modules.ads.dto.request.CreateAdsRequest;
import edu.ptithcm.learnnextbackend.modules.ads.dto.request.UpdateAdsRequest;
import edu.ptithcm.learnnextbackend.modules.ads.dto.response.AdsResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/ads")
public class AdminAdsController {

    @Autowired
    private AdsService adsService;

    @PostMapping
    public ResponseEntity<AdsResponse> create(@RequestBody @Valid CreateAdsRequest request) {
        return ResponseEntity.ok(adsService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AdsResponse>> getAll() {
        return ResponseEntity.ok(adsService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdsResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateAdsRequest request
    ) {
        return ResponseEntity.ok(adsService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
