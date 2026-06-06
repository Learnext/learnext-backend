package edu.ptithcm.learnnextbackend.modules.order;

import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    List<Order> findAllByPaymentCode(String paymentCode);

    @EntityGraph(attributePaths = {"course"})
    @Query("SELECT o FROM Order o WHERE o.course.instructor.id = :instructorId ORDER BY o.createdAt DESC")
    List<Order> findByCourseInstructorIdOrderByCreatedAtDesc(@Param("instructorId") UUID instructorId);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE o.course.instructor.id = :instructorId AND o.status = :status")
    BigDecimal sumPaidAmountByInstructorId(@Param("instructorId") UUID instructorId, @Param("status") OrderStatus status);
}
