package edu.ptithcm.learnnextbackend.modules.payment.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.cart.CartRepository;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.enrollment.entity.Enrollment;
import edu.ptithcm.learnnextbackend.modules.order.OrderItemRepository;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.entity.OrderItem;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.payment.PaymentRepository;
import edu.ptithcm.learnnextbackend.modules.payment.PaymentService;
import edu.ptithcm.learnnextbackend.modules.payment.dto.request.PaymentCallbackRequest;
import edu.ptithcm.learnnextbackend.modules.payment.dto.response.PaymentResponse;
import edu.ptithcm.learnnextbackend.modules.payment.entity.Payment;
import edu.ptithcm.learnnextbackend.modules.payment.enums.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public PaymentResponse mockPay(UUID orderId) {
        PaymentCallbackRequest request = new PaymentCallbackRequest();
        request.setOrderId(orderId);
        request.setSuccess(true);
        request.setTransactionCode("MOCK-" + System.currentTimeMillis());

        return handleCallback(request);
    }

    @Override
    public PaymentResponse handleCallback(PaymentCallbackRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BadRequestException("Order not found"));

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new BadRequestException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return toResponse(payment);
        }

        if (request.isSuccess()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionCode(request.getTransactionCode());
            payment.setPaidAt(LocalDateTime.now());

            order.setStatus(OrderStatus.PAID);

            createEnrollments(order);

            cartRepository.deleteByUser(order.getUser());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.FAILED);
        }

        orderRepository.save(order);
        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    private void createEnrollments(Order order) {
        for (OrderItem item : orderItemRepository.findByOrder(order)) {
            boolean exists = enrollmentRepository.existsByUserAndCourse(
                    order.getUser(),
                    item.getCourse()
            );

            if (!exists) {
                Enrollment enrollment = Enrollment.builder()
                        .user(order.getUser())
                        .course(item.getCourse())
                        .build();

                enrollmentRepository.save(enrollment);
            }
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .paymentUrl(payment.getPaymentUrl())
                .build();
    }
}
