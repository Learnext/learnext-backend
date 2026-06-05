package edu.ptithcm.learnnextbackend.modules.favorite;

import edu.ptithcm.learnnextbackend.modules.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Favorite> findByUserIdAndCourseId(UUID userId, UUID courseId);

    void deleteByUserIdAndCourseId(UUID userId, UUID courseId);
}
