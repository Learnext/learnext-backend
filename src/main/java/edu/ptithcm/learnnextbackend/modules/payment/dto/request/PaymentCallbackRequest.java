package edu.ptithcm.learnnextbackend.modules.payment.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentCallbackRequest {

    private UUID orderId;
    private String transactionCode;
    private boolean success;
}
