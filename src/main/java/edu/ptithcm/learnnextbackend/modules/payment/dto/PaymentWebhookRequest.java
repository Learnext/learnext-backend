package edu.ptithcm.learnnextbackend.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentWebhookRequest {
    @NotBlank(message = "Payment code is required")
    private String paymentCode;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
