package edu.ptithcm.learnnextbackend.modules.learning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "is_preview", nullable = false)
    private boolean preview;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}