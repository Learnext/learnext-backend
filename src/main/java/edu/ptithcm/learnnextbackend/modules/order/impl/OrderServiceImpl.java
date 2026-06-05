package edu.ptithcm.learnnextbackend.modules.order.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderService;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.CreateOrderRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.SubmitPaymentProofRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.order.mapper.OrderMapper;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CourseRepository courseRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Course course = courseRepository.findByIdAndStatus(request.getCourseId(), CourseStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        Order order = Order.builder()
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse submitPaymentProof(UUID userId, UUID orderId, SubmitPaymentProofRequest request) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.REJECTED) {
            throw new BadRequestException("Payment proof cannot be submitted for this order status");
        }

        order.setPaymentProofUrl(request.getPaymentProofUrl().trim());
        order.setStatus(OrderStatus.PROOF_SUBMITTED);

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public List<OrderResponse> getOrderHistory(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }
}
