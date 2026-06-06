package edu.ptithcm.learnnextbackend.modules.order;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.CreateOrderRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.SubmitPaymentProofRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.CartCheckoutResponse;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createOrder(userId, request)));
    }

    @PostMapping("/cart")
    public ResponseEntity<ApiResponse<CartCheckoutResponse>> createCartOrder(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid CreateOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createCartOrder(userId, request)));
    }

    @PostMapping("/{orderId}/proof")
    public ResponseEntity<ApiResponse<OrderResponse>> submitPaymentProof(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID orderId,
            @RequestBody @Valid SubmitPaymentProofRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.submitPaymentProof(userId, orderId, request.getPaymentProofUrl())
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderHistory(
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderHistory(userId)));
    }
}
