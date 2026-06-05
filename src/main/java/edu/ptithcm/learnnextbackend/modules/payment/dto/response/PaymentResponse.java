package edu.ptithcm.learnnextbackend.modules.payment.dto.response;

import edu.ptithcm.learnnextbackend.modules.payment.enums.PaymentMethod;
import edu.ptithcm.learnnextbackend.modules.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String paymentUrl;
}
