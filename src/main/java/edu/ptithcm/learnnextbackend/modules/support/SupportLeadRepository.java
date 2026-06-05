package edu.ptithcm.learnnextbackend.modules.support;

import edu.ptithcm.learnnextbackend.modules.support.entity.SupportLead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupportLeadRepository extends JpaRepository<SupportLead, UUID> {
    List<SupportLead> findAllByOrderByCreatedAtDesc();
}
