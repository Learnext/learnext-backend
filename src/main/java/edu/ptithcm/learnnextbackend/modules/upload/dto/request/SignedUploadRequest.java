package edu.ptithcm.learnnextbackend.modules.upload.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignedUploadRequest {
    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Content type is required")
    private String contentType;

    @Min(value = 1, message = "File size is required")
    private long size;
}
