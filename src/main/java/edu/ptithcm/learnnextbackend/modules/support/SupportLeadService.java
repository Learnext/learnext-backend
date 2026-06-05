package edu.ptithcm.learnnextbackend.modules.support;

import edu.ptithcm.learnnextbackend.modules.support.dto.request.SupportLeadRequest;
import edu.ptithcm.learnnextbackend.modules.support.dto.response.SupportLeadResponse;

import java.util.List;

public interface SupportLeadService {
    SupportLeadResponse create(SupportLeadRequest request);

    List<SupportLeadResponse> list();
}
