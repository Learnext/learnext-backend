package edu.ptithcm.learnnextbackend.modules.admin.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.infrastructure.mail.MailService;
import edu.ptithcm.learnnextbackend.modules.activation.ActivationCodeRepository;
import edu.ptithcm.learnnextbackend.modules.activation.entity.ActivationCode;
import edu.ptithcm.learnnextbackend.modules.admin.AdminPaymentService;
import edu.ptithcm.learnnextbackend.modules.admin.dto.response.AdminPaymentResponse;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.order.mapper.OrderMapper;
import edu.ptithcm.learnnextbackend.modules.payment.PaymentSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminPaymentServiceImpl implements AdminPaymentService {
    private final OrderRepository orderRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final MailService mailService;
    private final PaymentSettlementService paymentSettlementService;

    @Autowired
    public AdminPaymentServiceImpl(
            OrderRepository orderRepository,
            ActivationCodeRepository activationCodeRepository,
            MailService mailService,
            PaymentSettlementService paymentSettlementService
    ) {
        this.orderRepository = orderRepository;
        this.activationCodeRepository = activationCodeRepository;
        this.mailService = mailService;
        this.paymentSettlementService = paymentSettlementService;
    }

    public AdminPaymentServiceImpl(
            OrderRepository orderRepository,
            ActivationCodeRepository activationCodeRepository,
            MailService mailService
    ) {
        this(orderRepository, activationCodeRepository, mailService, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AdminPaymentResponse confirmPayment(UUID orderId) {
        Order order = requireOrder(orderId);
        return confirmOrder(order, false);
    }

    @Override
    @Transactional
    public List<AdminPaymentResponse> confirmPaymentCode(String paymentCode) {
        List<Order> orders = orderRepository.findAllByPaymentCode(paymentCode.trim());
        if (orders.isEmpty()) {
            throw new NotFoundException("Order not found");
        }
        return orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.PAID)
                .map(order -> confirmOrder(order, true))
                .toList();
    }

    private AdminPaymentResponse confirmOrder(Order order, boolean allowPendingPayment) {
        if (order.getStatus() != OrderStatus.PROOF_SUBMITTED
                && (!allowPendingPayment || order.getStatus() != OrderStatus.PENDING_PAYMENT)) {
            throw new BadRequestException("Only proof submitted orders can be confirmed");
        }
        Order savedOrder;
        if (paymentSettlementService == null) {
            order.setStatus(OrderStatus.PAID);
            savedOrder = orderRepository.save(order);
        } else {
            savedOrder = paymentSettlementService.settlePaidOrder(order);
        }
        ActivationCode activationCode;
        if (activationCodeRepository.existsByOrderId(savedOrder.getId())) {
            activationCode = activationCodeRepository.findByOrderId(savedOrder.getId())
                    .orElseThrow(() -> new NotFoundException("Activation code not found"));
        } else {
            activationCode = activationCodeRepository.save(ActivationCode.builder()
                    .code(UUID.randomUUID().toString())
                    .order(savedOrder)
                    .user(savedOrder.getUser())
                    .course(savedOrder.getCourse())
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .build());
            mailService.sendActivationCode(
                    savedOrder.getUser().getEmail(),
                    savedOrder.getUser().getFullName(),
                    savedOrder.getCourse().getTitle(),
                    activationCode.getCode()
            );
        }

        return AdminPaymentResponse.builder()
                .order(OrderMapper.toResponse(savedOrder))
                .activationCode(activationCode.getCode())
                .build();
    }

    @Override
    @Transactional
    public OrderResponse rejectPayment(UUID orderId) {
        Order order = requireOrder(orderId);
        if (order.getStatus() != OrderStatus.PROOF_SUBMITTED) {
            throw new BadRequestException("Only proof submitted orders can be rejected");
        }

        order.setStatus(OrderStatus.REJECTED);
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    private Order requireOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }
}
