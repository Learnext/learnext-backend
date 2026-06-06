package edu.ptithcm.learnnextbackend.modules.order.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderService;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.CreateOrderRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.SubmitPaymentProofRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.CartCheckoutResponse;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.order.mapper.OrderMapper;
import edu.ptithcm.learnnextbackend.modules.payment.PaymentQrProperties;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentQrProperties paymentQrProperties;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            PaymentQrProperties paymentQrProperties
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.paymentQrProperties = paymentQrProperties;
    }

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CourseRepository courseRepository
    ) {
        this(orderRepository, userRepository, courseRepository, null, new PaymentQrProperties("970423", "10000232922", "LEARNEXT"));
    }

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Course course = courseRepository.findByIdAndStatus(request.getCourseId(), CourseStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (enrollmentRepository != null && enrollmentRepository.existsByUserIdAndCourseId(userId, course.getId())) {
            throw new BadRequestException("Bạn đã đăng ký khóa học này rồi");
        }

        Order order = Order.builder()
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .paymentCode("LNX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved, paymentQrProperties.qrImageUrl(saved.getAmount(), saved.getPaymentCode()));
    }

    @Override
    @Transactional
    public OrderResponse submitPaymentProof(UUID userId, UUID orderId, String paymentProofUrl) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.REJECTED) {
            throw new BadRequestException("Payment proof cannot be submitted for this order status");
        }

        if (paymentProofUrl == null || paymentProofUrl.isBlank()) {
            throw new BadRequestException("Payment proof URL is required");
        }

        order.setPaymentProofUrl(paymentProofUrl.trim());
        order.setStatus(OrderStatus.PROOF_SUBMITTED);

        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved, paymentQrProperties.qrImageUrl(saved.getAmount(), saved.getPaymentCode()));
    }

    @Override
    @Transactional
    public CartCheckoutResponse createCartOrder(UUID userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        final List<UUID> rawIds = expandCourseIds(request);
        if (rawIds.isEmpty() || rawIds.stream().anyMatch(id -> id == null)) {
            throw new BadRequestException("At least one course id is required");
        }

        // Loại bỏ các khóa học user đã đăng ký
        final List<UUID> courseIds;
        if (enrollmentRepository != null) {
            List<UUID> filtered = rawIds.stream()
                    .filter(id -> !enrollmentRepository.existsByUserIdAndCourseId(userId, id))
                    .toList();
            if (filtered.isEmpty()) {
                throw new BadRequestException("Bạn đã đăng ký tất cả các khóa học trong giỏ hàng");
            }
            courseIds = filtered;
        } else {
            courseIds = rawIds;
        }

        String paymentCode = "LNX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        List<Order> orders = courseIds.stream()
                .map(courseId -> {
                    Course course = courseRepository.findByIdAndStatus(courseId, CourseStatus.PUBLISHED)
                            .orElseThrow(() -> new NotFoundException("Course not found"));
                    return orderRepository.save(Order.builder()
                            .user(user)
                            .course(course)
                            .amount(course.getPrice())
                            .paymentCode(paymentCode)
                            .status(OrderStatus.PENDING_PAYMENT)
                            .build());
                })
                .toList();

        BigDecimal totalAmount = orders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String qrImageUrl = paymentQrProperties.qrImageUrl(totalAmount, paymentCode);
        return CartCheckoutResponse.builder()
                .paymentCode(paymentCode)
                .qrImageUrl(qrImageUrl)
                .amount(totalAmount)
                .orders(orders.stream()
                        .map(order -> OrderMapper.toResponse(order, qrImageUrl))
                        .toList())
                .build();
    }

    private List<UUID> expandCourseIds(CreateOrderRequest request) {
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Mỗi khóa học chỉ tính 1 lần, không nhân theo quantity
            LinkedHashSet<UUID> seen = new LinkedHashSet<>();
            request.getItems().forEach(item -> {
                if (item.getCourseId() != null) seen.add(item.getCourseId());
            });
            return new ArrayList<>(seen);
        }
        if (request.getCourseIds() != null && !request.getCourseIds().isEmpty()) {
            return new LinkedHashSet<>(request.getCourseIds()).stream().toList();
        }
        if (request.getCourseId() != null) {
            return List.of(request.getCourseId());
        }
        return List.of();
    }

    @Transactional
    public OrderResponse submitPaymentProof(UUID userId, UUID orderId, SubmitPaymentProofRequest request) {
        return submitPaymentProof(userId, orderId, request.getPaymentProofUrl());
    }

    @Override
    public List<OrderResponse> getOrderHistory(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(order -> OrderMapper.toResponse(order, paymentQrProperties.qrImageUrl(order.getAmount(), order.getPaymentCode())))
                .toList();
    }
}
