package edu.ptithcm.learnnextbackend.modules.invoice;

import edu.ptithcm.learnnextbackend.modules.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    boolean existsByOrderId(UUID orderId);

    Optional<Invoice> findByOrderId(UUID orderId);

    List<Invoice> findByUserIdOrderByIssuedAtDesc(UUID userId);
}
