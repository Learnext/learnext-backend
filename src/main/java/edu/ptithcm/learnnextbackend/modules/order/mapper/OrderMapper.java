package edu.ptithcm.learnnextbackend.modules.order.mapper;

import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return toResponse(order, null);
    }

    public static OrderResponse toResponse(Order order, String qrImageUrl) {
        return OrderResponse.builder()
                .id(order.getId())
                .courseId(order.getCourse().getId())
                .courseTitle(order.getCourse().getTitle())
                .amount(order.getAmount())
                .paymentProofUrl(order.getPaymentProofUrl())
                .paymentCode(order.getPaymentCode())
                .qrImageUrl(qrImageUrl)
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .paidAt(order.getPaidAt())
                .build();
    }
}
