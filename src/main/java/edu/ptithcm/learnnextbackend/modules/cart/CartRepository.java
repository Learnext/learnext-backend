package edu.ptithcm.learnnextbackend.modules.cart;

import edu.ptithcm.learnnextbackend.modules.cart.entity.CartItem;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByUserOrderByCreatedAtDesc(User user);

    boolean existsByUserAndCourse(User user, Course course);

    Optional<CartItem> findByUserAndCourse(User user, Course course);

    void deleteByUser(User user);

}
