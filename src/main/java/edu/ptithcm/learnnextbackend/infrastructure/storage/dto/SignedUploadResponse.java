package edu.ptithcm.learnnextbackend.infrastructure.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignedUploadResponse {
    private String objectKey;
    private String uploadUrl;
    private String publicUrl;
}
