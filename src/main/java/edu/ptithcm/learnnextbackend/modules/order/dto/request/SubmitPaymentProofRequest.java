package edu.ptithcm.learnnextbackend.modules.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitPaymentProofRequest {
    @NotBlank(message = "Payment proof URL is required")
    @Size(max = 1000, message = "Payment proof URL must be at most 1000 characters")
    private String paymentProofUrl;
}
