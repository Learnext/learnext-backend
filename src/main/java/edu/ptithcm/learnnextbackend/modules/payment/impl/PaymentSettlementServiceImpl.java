package edu.ptithcm.learnnextbackend.modules.payment.impl;

import edu.ptithcm.learnnextbackend.infrastructure.mail.MailService;
import edu.ptithcm.learnnextbackend.modules.activation.ActivationCodeRepository;
import edu.ptithcm.learnnextbackend.modules.activation.entity.ActivationCode;
import edu.ptithcm.learnnextbackend.modules.enrollment.EnrollmentRepository;
import edu.ptithcm.learnnextbackend.modules.invoice.InvoiceRepository;
import edu.ptithcm.learnnextbackend.modules.invoice.entity.Invoice;
import edu.ptithcm.learnnextbackend.modules.order.OrderRepository;
import edu.ptithcm.learnnextbackend.modules.order.entity.Order;
import edu.ptithcm.learnnextbackend.modules.order.enums.OrderStatus;
import edu.ptithcm.learnnextbackend.modules.payment.PaymentSettlementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentSettlementServiceImpl implements PaymentSettlementService {
    private final OrderRepository orderRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final MailService mailService;
    private final String frontendBaseUrl;

    public PaymentSettlementServiceImpl(
            OrderRepository orderRepository,
            EnrollmentRepository enrollmentRepository,
            InvoiceRepository invoiceRepository,
            ActivationCodeRepository activationCodeRepository,
            MailService mailService,
            @Value("${app.frontend-base-url:http://localhost:5173}") String frontendBaseUrl
    ) {
        this.orderRepository = orderRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.activationCodeRepository = activationCodeRepository;
        this.mailService = mailService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    @Transactional
    public Order settlePaidOrder(Order order) {
        order.setStatus(OrderStatus.PAID);
        if (order.getPaidAt() == null) {
            order.setPaidAt(LocalDateTime.now());
        }
        Order saved = orderRepository.save(order);

        if (!enrollmentRepository.existsByUserIdAndCourseId(saved.getUser().getId(), saved.getCourse().getId())
                && !activationCodeRepository.existsByOrderId(saved.getId())) {
            ActivationCode activationCode = activationCodeRepository.save(ActivationCode.builder()
                    .code(UUID.randomUUID().toString())
                    .order(saved)
                    .user(saved.getUser())
                    .course(saved.getCourse())
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .build());
            mailService.sendActivationCode(
                    saved.getUser().getEmail(),
                    saved.getUser().getFullName(),
                    saved.getCourse().getTitle(),
                    activationCode.getCode(),
                    frontendBaseUrl.replaceAll("/$", "") + "/activate?code=" + activationCode.getCode()
            );
        }
        if (!invoiceRepository.existsByOrderId(saved.getId())) {
            invoiceRepository.save(Invoice.builder()
                    .order(saved)
                    .user(saved.getUser())
                    .course(saved.getCourse())
                    .amount(saved.getAmount())
                    .build());
        }

        return saved;
    }
}
