package edu.ptithcm.learnnextbackend.modules.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequest {
    @NotNull(message = "Course id is required")
    private UUID courseId;
}
