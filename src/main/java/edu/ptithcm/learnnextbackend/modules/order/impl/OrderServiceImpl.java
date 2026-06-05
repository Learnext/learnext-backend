package edu.ptithcm.learnnextbackend.modules.order.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.cart.CartRepository;
import edu.ptithcm.learnnextbackend.modules.cart.entity.CartItem;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderItemRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderService;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.CheckoutRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderItemResponse;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.entity.OrderItem;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.payment.PaymentRepository;
import edu.ptithcm.learnnextbackend.modules.payment.entity.Payment;
import edu.ptithcm.learnnextbackend.modules.payment.enums.PaymentStatus;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public OrderResponse checkout(CheckoutRequest request) {
        User user = getCurrentUser();

        List<CartItem> cartItems = cartRepository.findByUserOrderByCreatedAtDesc(user);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        List<CartItem> validItems = cartItems.stream()
                .filter(item -> !enrollmentRepository.existsByUserAndCourse(user, item.getCourse()))
                .toList();

        if (validItems.isEmpty()) {
            throw new BadRequestException("All courses in cart are already purchased");
        }

        BigDecimal totalAmount = validItems.stream()
                .map(item -> item.getCourse().getPrice() != null
                        ? item.getCourse().getPrice()
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = validItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(savedOrder)
                        .course(cartItem.getCourse())
                        .courseTitle(cartItem.getCourse().getTitle())
                        .price(cartItem.getCourse().getPrice() != null
                                ? cartItem.getCourse().getPrice()
                                : BigDecimal.ZERO)
                        .build())
                .toList();

        orderItemRepository.saveAll(orderItems);

        Payment payment = Payment.builder()
                .order(savedOrder)
                .amount(totalAmount)
                .method(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .paymentUrl(generateMockPaymentUrl(savedOrder.getId()))
                .build();

        paymentRepository.save(payment);

        return toOrderResponse(savedOrder, orderItems, payment);
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        User user = getCurrentUser();

        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrder(order);
                    Payment payment = paymentRepository.findByOrder(order).orElse(null);
                    return toOrderResponse(order, items, payment);
                })
                .toList();
    }

    @Override
    public OrderResponse getOrder(UUID orderId) {
        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to view this order");
        }

        List<OrderItem> items = orderItemRepository.findByOrder(order);
        Payment payment = paymentRepository.findByOrder(order).orElse(null);

        return toOrderResponse(order, items, payment);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    private String generateMockPaymentUrl(UUID orderId) {
        return "http://localhost:1201/api/v1/payments/mock-pay?orderId=" + orderId;
    }

    private OrderResponse toOrderResponse(Order order, List<OrderItem> items, Payment payment) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus())
                .paymentMethod(payment != null ? payment.getMethod() : null)
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .paymentUrl(payment != null ? payment.getPaymentUrl() : null)
                .items(items.stream()
                        .map(this::toOrderItemResponse)
                        .toList())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .courseId(item.getCourse().getId())
                .courseTitle(item.getCourseTitle())
                .price(item.getPrice())
                .build();
    }
}
