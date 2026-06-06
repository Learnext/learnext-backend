package edu.ptithcm.learnnextbackend.modules.admin;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.admin.dto.response.AdminPaymentResponse;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminPaymentController {
    private final AdminPaymentService adminPaymentService;
    private final String adminKey;

    public AdminPaymentController(
            AdminPaymentService adminPaymentService,
            @Value("${admin.key:dev-admin-key}") String adminKey
    ) {
        this.adminPaymentService = adminPaymentService;
        this.adminKey = adminKey;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @RequestHeader("X-Admin-Key") String providedAdminKey
    ) {
        requireAdminKey(providedAdminKey);
        return ResponseEntity.ok(ApiResponse.success(adminPaymentService.getOrders()));
    }

    @PatchMapping("/confirm-by-code")
    public ResponseEntity<ApiResponse<List<AdminPaymentResponse>>> confirmPaymentCode(
            @RequestHeader("X-Admin-Key") String providedAdminKey,
            @RequestParam String paymentCode
    ) {
        requireAdminKey(providedAdminKey);
        return ResponseEntity.ok(ApiResponse.success(adminPaymentService.confirmPaymentCode(paymentCode)));
    }

    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<AdminPaymentResponse>> confirmPayment(
            @RequestHeader("X-Admin-Key") String providedAdminKey,
            @PathVariable UUID orderId
    ) {
        requireAdminKey(providedAdminKey);
        return ResponseEntity.ok(ApiResponse.success(adminPaymentService.confirmPayment(orderId)));
    }

    @PatchMapping("/{orderId}/reject")
    public ResponseEntity<ApiResponse<OrderResponse>> rejectPayment(
            @RequestHeader("X-Admin-Key") String providedAdminKey,
            @PathVariable UUID orderId
    ) {
        requireAdminKey(providedAdminKey);
        return ResponseEntity.ok(ApiResponse.success(adminPaymentService.rejectPayment(orderId)));
    }

    private void requireAdminKey(String providedAdminKey) {
        if (!adminKey.equals(providedAdminKey)) {
            throw new BadRequestException("Invalid admin key");
        }
    }
}
