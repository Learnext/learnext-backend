package edu.ptithcm.learnnextbackend.infrastructure.storage;

import edu.ptithcm.learnnextbackend.infrastructure.storage.dto.SignedUploadResponse;

public interface StorageService {
    SignedUploadResponse createSignedUploadUrl(String fileName, String contentType, long size);
}
