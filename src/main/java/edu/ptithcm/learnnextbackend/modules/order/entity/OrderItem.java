package edu.ptithcm.learnnextbackend.modules.order.entity;

import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}
