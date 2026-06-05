package edu.ptithcm.learnnextbackend.modules.order.mapper;

import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .courseId(order.getCourse().getId())
                .courseTitle(order.getCourse().getTitle())
                .amount(order.getAmount())
                .paymentProofUrl(order.getPaymentProofUrl())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
