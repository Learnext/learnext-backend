package edu.ptithcm.learnnextbackend.modules.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private UUID id;
    private UUID courseId;
    private String courseTitle;
    private BigDecimal amount;
    private String paymentProofUrl;
    private String status;
    private LocalDateTime createdAt;
}
