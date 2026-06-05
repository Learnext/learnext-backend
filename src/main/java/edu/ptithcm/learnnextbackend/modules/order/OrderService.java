package edu.ptithcm.learnnextbackend.modules.order;

import edu.ptithcm.learnnextbackend.modules.order.dto.request.CheckoutRequest;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse checkout(CheckoutRequest request);
    List<OrderResponse> getMyOrders();
    OrderResponse getOrder(UUID orderId);
}
