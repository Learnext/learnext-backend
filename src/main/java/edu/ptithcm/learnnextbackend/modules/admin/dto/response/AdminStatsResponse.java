package edu.ptithcm.learnnextbackend.modules.admin.dto.response;

import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AdminStatsResponse {
    private Map<String, Long> ordersByStatus;
    private BigDecimal totalRevenue;
    private List<OrderResponse> recentTransactions;
}
