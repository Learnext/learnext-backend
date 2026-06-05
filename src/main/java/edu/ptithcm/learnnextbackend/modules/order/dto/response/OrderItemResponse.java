package edu.ptithcm.learnnextbackend.modules.order.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemResponse {

    private UUID courseId;
    private String courseTitle;
    private BigDecimal price;
}
