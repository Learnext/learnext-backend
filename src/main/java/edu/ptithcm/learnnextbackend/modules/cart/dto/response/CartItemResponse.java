package edu.ptithcm.learnnextbackend.modules.cart.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CartItemResponse {

    private UUID id;

    private UUID courseId;

    private String courseTitle;

    private String thumbnailUrl;

    private BigDecimal price;
}
