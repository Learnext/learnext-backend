package edu.ptithcm.learnnextbackend.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentSimulationRequest {
    @NotBlank(message = "Payment code is required")
    private String paymentCode;
}
