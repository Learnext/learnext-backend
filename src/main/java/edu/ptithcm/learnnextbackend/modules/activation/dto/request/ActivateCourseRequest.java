package edu.ptithcm.learnnextbackend.modules.activation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateCourseRequest {
    @NotBlank(message = "Activation code is required")
    private String activationCode;
}
