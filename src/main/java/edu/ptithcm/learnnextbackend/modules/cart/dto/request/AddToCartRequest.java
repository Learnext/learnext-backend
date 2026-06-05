package edu.ptithcm.learnnextbackend.modules.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddToCartRequest {

    @NotNull(message = "Course id is required")
    private UUID courseId;

}
