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

    public AdminPaymentServiceImpl(
            OrderRepository orderRepository,
            ActivationCodeRepository activationCodeRepository,
            MailService mailService
    ) {
        this.orderRepository = orderRepository;
        this.activationCodeRepository = activationCodeRepository;
        this.mailService = mailService;
    }

    @Override
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
        if (order.getStatus() != OrderStatus.PROOF_SUBMITTED) {
            throw new BadRequestException("Only proof submitted orders can be confirmed");
        }
        if (activationCodeRepository.existsByOrderId(orderId)) {
            throw new BadRequestException("Activation code already exists for this order");
        }

        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);
        ActivationCode activationCode = activationCodeRepository.save(ActivationCode.builder()
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
