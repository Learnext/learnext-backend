package edu.ptithcm.learnnextbackend.modules.order;

import edu.ptithcm.learnnextbackend.modules.order.dto.request.CreateOrderRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.request.SubmitPaymentProofRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID userId, CreateOrderRequest request);

    OrderResponse submitPaymentProof(UUID userId, UUID orderId, SubmitPaymentProofRequest request);

    List<OrderResponse> getOrderHistory(UUID userId);
}
