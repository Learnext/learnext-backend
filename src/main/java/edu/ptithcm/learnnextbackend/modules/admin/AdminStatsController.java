package edu.ptithcm.learnnextbackend.modules.admin;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.admin.dto.response.AdminStatsResponse;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/stats")
public class AdminStatsController {
    private final OrderRepository orderRepository;
    private final String adminKey;

    public AdminStatsController(
            OrderRepository orderRepository,
            @Value("${admin.key:dev-admin-key}") String adminKey
    ) {
        this.orderRepository = orderRepository;
        this.adminKey = adminKey;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats(
            @RequestHeader("X-Admin-Key") String providedKey
    ) {
        if (!adminKey.equals(providedKey)) throw new BadRequestException("Invalid admin key");

        List<Order> allOrders = orderRepository.findAll();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            byStatus.put(s.name(), allOrders.stream().filter(o -> o.getStatus() == s).count());
        }

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OrderResponse> recent = allOrders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(30)
                .map(OrderMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(AdminStatsResponse.builder()
                .ordersByStatus(byStatus)
                .totalRevenue(totalRevenue)
                .recentTransactions(recent)
                .build()));
    }
}
