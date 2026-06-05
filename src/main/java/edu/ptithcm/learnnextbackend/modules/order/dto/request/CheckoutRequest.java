package edu.ptithcm.learnnextbackend.modules.order.dto.request;

import edu.ptithcm.learnnextbackend.modules.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
