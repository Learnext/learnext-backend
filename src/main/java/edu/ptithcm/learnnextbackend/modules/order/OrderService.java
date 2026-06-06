package edu.ptithcm.learnnextbackend.modules.order;

import edu.ptithcm.learnnextbackend.modules.order.dto.request.CreateOrderRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.CartCheckoutResponse;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID userId, CreateOrderRequest request);

    CartCheckoutResponse createCartOrder(UUID userId, CreateOrderRequest request);

    OrderResponse submitPaymentProof(UUID userId, UUID orderId, String paymentProofUrl);

    List<OrderResponse> getOrderHistory(UUID userId);
}
