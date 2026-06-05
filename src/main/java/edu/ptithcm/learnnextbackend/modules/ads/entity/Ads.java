package edu.ptithcm.learnnextbackend.modules.ads.entity;

import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsPlacement;
import edu.ptithcm.learnnextbackend.modules.ads.enums.AdsStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "target_url")
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdsPlacement placement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdsStatus status;

    private Integer priority;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
