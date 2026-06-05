package edu.ptithcm.learnnextbackend.modules.upload;

import edu.ptithcm.learnnextbackend.common.core.dto.ApiResponse;
import edu.ptithcm.learnnextbackend.infrastructure.storage.StorageService;
import edu.ptithcm.learnnextbackend.infrastructure.storage.dto.SignedUploadResponse;
import edu.ptithcm.learnnextbackend.modules.upload.dto.request.SignedUploadRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/uploads")
public class UploadController {
    private final StorageService storageService;

    public UploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/signed-url")
    public ResponseEntity<ApiResponse<SignedUploadResponse>> createSignedUploadUrl(
            @RequestBody @Valid SignedUploadRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(storageService.createSignedUploadUrl(
                request.getFileName(),
                request.getContentType(),
                request.getSize()
        )));
    }
}
