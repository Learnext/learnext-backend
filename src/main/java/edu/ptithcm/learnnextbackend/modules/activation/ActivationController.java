package edu.ptithcm.learnnextbackend.modules.activation;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.modules.activation.dto.request.ActivateCourseRequest;
import edu.ptithcm.learnnextbackend.modules.enrollment.dto.response.EnrollmentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activations")
public class ActivationController {
    private final ActivationService activationService;

    public ActivationController(ActivationService activationService) {
        this.activationService = activationService;
    }

    @PostMapping("/activate")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> activateCourse(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid ActivateCourseRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(activationService.activateCourse(userId, request)));
    }
}
