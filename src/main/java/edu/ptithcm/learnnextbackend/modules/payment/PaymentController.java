package edu.ptithcm.learnnextbackend.modules.payment;

import edu.ptithcm.learnnextbackend.modules.payment.dto.request.PaymentCallbackRequest;
import edu.ptithcm.learnnextbackend.modules.payment.dto.response.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/mock-pay")
    public ResponseEntity<PaymentResponse> mockPay(@RequestParam UUID orderId) {
        return ResponseEntity.ok(paymentService.mockPay(orderId));
    }

    @PostMapping("/callback")
    public ResponseEntity<PaymentResponse> callback(@RequestBody PaymentCallbackRequest request) {
        return ResponseEntity.ok(paymentService.handleCallback(request));
    }
}
