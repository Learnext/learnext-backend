package edu.ptithcm.learnnextbackend.modules.support.impl;

import edu.ptithcm.learnnextbackend.modules.support.SupportLeadRepository;
import edu.ptithcm.learnnextbackend.modules.support.SupportLeadService;
import edu.ptithcm.learnnextbackend.modules.support.dto.request.SupportLeadRequest;
import edu.ptithcm.learnnextbackend.modules.support.dto.response.SupportLeadResponse;
import edu.ptithcm.learnnextbackend.modules.support.entity.SupportLead;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupportLeadServiceImpl implements SupportLeadService {
    private final SupportLeadRepository supportLeadRepository;

    public SupportLeadServiceImpl(SupportLeadRepository supportLeadRepository) {
        this.supportLeadRepository = supportLeadRepository;
    }

    @Override
    public SupportLeadResponse create(SupportLeadRequest request) {
        SupportLead lead = supportLeadRepository.save(SupportLead.builder()
                .name(request.getName())
                .email(request.getEmail())
                .category(request.getCategory())
                .subject(request.getSubject())
                .message(request.getMessage())
                .build());
        return toResponse(lead);
    }

    @Override
    public List<SupportLeadResponse> list() {
        return supportLeadRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SupportLeadResponse toResponse(SupportLead lead) {
        return SupportLeadResponse.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .category(lead.getCategory())
                .subject(lead.getSubject())
                .message(lead.getMessage())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .build();
    }
}
