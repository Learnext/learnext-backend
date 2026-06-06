package edu.ptithcm.learnnextbackend.modules.invoice.dto;

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
public class InvoiceResponse {
    private UUID id;
    private String invoiceNo;
    private UUID orderId;
    private UUID courseId;
    private String courseTitle;
    private BigDecimal amount;
    private LocalDateTime issuedAt;
}
