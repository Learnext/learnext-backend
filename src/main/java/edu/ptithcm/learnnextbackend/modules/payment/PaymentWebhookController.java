package edu.ptithcm.learnnextbackend.modules.payment;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.order.mapper.OrderMapper;
import edu.ptithcm.learnnextbackend.modules.payment.dto.PaymentWebhookRequest;
import edu.ptithcm.learnnextbackend.modules.payment.dto.PaymentSimulationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {
    private final OrderRepository orderRepository;
    private final PaymentSettlementService paymentSettlementService;
    private final String adminKey;

    public PaymentWebhookController(
            OrderRepository orderRepository,
            PaymentSettlementService paymentSettlementService,
            @Value("${admin.key:dev-admin-key}") String adminKey
    ) {
        this.orderRepository = orderRepository;
        this.paymentSettlementService = paymentSettlementService;
        this.adminKey = adminKey;
    }

    @PostMapping("/simulate-confirm")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> simulateConfirm(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid PaymentSimulationRequest request
    ) {
        var orders = orderRepository.findAllByPaymentCode(request.getPaymentCode().trim());
        if (orders.isEmpty()) {
            throw new NotFoundException("Order not found");
        }
        if (orders.stream().anyMatch(order -> !order.getUser().getId().equals(userId))) {
            throw new BadRequestException("Payment code does not belong to current user");
        }

        var settled = orders.stream()
                .map(order -> order.getStatus() == OrderStatus.PAID ? order : paymentSettlementService.settlePaidOrder(order))
                .map(OrderMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(settled));
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmByPaymentCode(
            @RequestHeader("X-Admin-Key") String providedKey,
            @RequestBody @Valid PaymentWebhookRequest request
    ) {
        if (!adminKey.equals(providedKey)) {
            throw new BadRequestException("Invalid webhook key");
        }

        var orders = orderRepository.findAllByPaymentCode(request.getPaymentCode().trim());
        if (orders.isEmpty()) {
            throw new NotFoundException("Order not found");
        }
        var totalAmount = orders.stream()
                .map(Order::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        if (totalAmount.compareTo(request.getAmount()) != 0) {
            throw new BadRequestException("Payment amount does not match order amount");
        }
        Order first = orders.get(0);
        if (first.getStatus() == OrderStatus.PAID) {
            return ResponseEntity.ok(ApiResponse.success(OrderMapper.toResponse(first)));
        }
        orders.forEach(paymentSettlementService::settlePaidOrder);

        return ResponseEntity.ok(ApiResponse.success(OrderMapper.toResponse(first)));
    }
}
