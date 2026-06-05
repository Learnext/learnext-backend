package edu.ptithcm.learnnextbackend.modules.admin.dto.response;

import edu.ptithcm.learnnextbackend.modules.order.dto.response.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPaymentResponse {
    private OrderResponse order;
    private String activationCode;
}
