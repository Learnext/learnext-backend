package edu.ptithcm.learnnextbackend.modules.invoice;

import edu.ptithcm.learnnextbackend.modules.invoice.dto.InvoiceResponse;
import edu.ptithcm.learnnextbackend.modules.invoice.entity.Invoice;

public final class InvoiceMapper {
    private InvoiceMapper() {
    }

    public static InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNo(invoice.getInvoiceNo())
                .orderId(invoice.getOrder().getId())
                .courseId(invoice.getCourse().getId())
                .courseTitle(invoice.getCourse().getTitle())
                .amount(invoice.getAmount())
                .issuedAt(invoice.getIssuedAt())
                .build();
    }
}
