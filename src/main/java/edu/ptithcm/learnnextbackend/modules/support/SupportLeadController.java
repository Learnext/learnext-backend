package edu.ptithcm.learnnextbackend.modules.support;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.support.dto.request.SupportLeadRequest;
import edu.ptithcm.learnnextbackend.modules.support.dto.response.SupportLeadResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SupportLeadController {
    private final SupportLeadService supportLeadService;
    private final String adminKey;

    public SupportLeadController(SupportLeadService supportLeadService,
                                 @Value("${app.admin.key:}") String adminKey) {
        this.supportLeadService = supportLeadService;
        this.adminKey = adminKey;
    }

    @PostMapping("/support/leads")
    public ResponseEntity<ApiResponse<SupportLeadResponse>> create(@RequestBody @Valid SupportLeadRequest request) {
        return ResponseEntity.ok(ApiResponse.success(supportLeadService.create(request)));
    }

    @GetMapping("/admin/support/leads")
    public ResponseEntity<ApiResponse<List<SupportLeadResponse>>> list(@RequestHeader("X-Admin-Key") String key) {
        if (adminKey.isBlank() || !adminKey.equals(key)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid admin key");
        }

        return ResponseEntity.ok(ApiResponse.success(supportLeadService.list()));
    }
}
