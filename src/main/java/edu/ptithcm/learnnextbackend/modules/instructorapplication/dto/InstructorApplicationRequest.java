package edu.ptithcm.learnnextbackend.modules.instructorapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorApplicationRequest {
    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotBlank(message = "Phone is required")
    @Size(max = 30, message = "Phone is too long")
    private String phone;

    @NotBlank(message = "Certificate URL is required")
    @Size(max = 1000, message = "Certificate URL is too long")
    private String certificateUrl;

    private String bio;
}
