package edu.ptithcm.learnnextbackend.modules.admin;

import edu.ptithcm.learnnextbackend.modules.admin.dto.response.AdminPaymentResponse;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface AdminPaymentService {
    List<OrderResponse> getOrders();

    AdminPaymentResponse confirmPayment(UUID orderId);

    List<AdminPaymentResponse> confirmPaymentCode(String paymentCode);

    OrderResponse rejectPayment(UUID orderId);
}
