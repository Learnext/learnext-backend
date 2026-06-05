package edu.ptithcm.learnnextbackend.modules.order.dto.response;

import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.payment.enums.PaymentMethod;
import edu.ptithcm.learnnextbackend.modules.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {

    private UUID orderId;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentUrl;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;

}
