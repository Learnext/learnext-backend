package edu.ptithcm.learnnextbackend.modules.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCheckoutResponse {
    private String paymentCode;
    private String qrImageUrl;
    private BigDecimal amount;
    private List<OrderResponse> orders;
}
