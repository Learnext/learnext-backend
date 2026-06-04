package edu.ptithcm.learnnextbackend.modules.wishlist;

import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    boolean existsByUserAndCourse(User user, Course course);
    Optional<Wishlist> findByUserAndCourse(User user, Course course);
    List<Wishlist> findByUserOrderByCreatedAtDesc(User user);

}
