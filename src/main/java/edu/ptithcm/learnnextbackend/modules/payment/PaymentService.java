package edu.ptithcm.learnnextbackend.modules.payment;

import edu.ptithcm.learnnextbackend.modules.payment.dto.request.PaymentCallbackRequest;
import edu.ptithcm.learnnextbackend.modules.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse mockPay(UUID orderId);
    PaymentResponse handleCallback(PaymentCallbackRequest request);
}
