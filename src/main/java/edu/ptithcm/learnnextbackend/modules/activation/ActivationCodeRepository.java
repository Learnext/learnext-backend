package edu.ptithcm.learnnextbackend.modules.activation;

import edu.ptithcm.learnnextbackend.modules.activation.entity.ActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, UUID> {
    Optional<ActivationCode> findByCode(String code);

    boolean existsByOrderId(UUID orderId);
}
