package edu.ptithcm.learnnextbackend.modules.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequest {
    private UUID courseId;

    private List<UUID> courseIds;

    @Valid
    private List<CartItem> items;

    @Getter
    @Setter
    public static class CartItem {
        private UUID courseId;

        @Min(1)
        private int quantity = 1;
    }
}
