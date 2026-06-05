package edu.ptithcm.learnnextbackend.modules.ads;

import edu.ptithcm.learnnextbackend.modules.ads.entity.Ads;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdsRepository extends JpaRepository<Ads, UUID> {

    List<Ads> findByStatusAndPlacementOrderByPriorityDesc(AdsStatus status, AdsPlacement placement);

    List<Ads> findByStatusOrderByPriorityDesc(AdsStatus status);
}
