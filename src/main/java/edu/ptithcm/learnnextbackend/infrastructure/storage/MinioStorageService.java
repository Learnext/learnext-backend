package edu.ptithcm.learnnextbackend.infrastructure.storage;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.infrastructure.storage.dto.SignedUploadResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioStorageService implements StorageService {
    private static final long MAX_UPLOAD_SIZE = 100L * 1024L * 1024L;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "video/mp4",
            "video/webm",
            "video/ogg",
            "video/quicktime",
            "video/x-m4v",
            "video/x-msvideo",
            "video/x-matroska",
            "application/pdf"
    );

    private final MinioClient minioClient;
    private final String bucket;
    private final String publicBaseUrl;

    public MinioStorageService(
            @Value("${storage.minio.endpoint:http://localhost:9000}") String endpoint,
            @Value("${storage.minio.access-key:minioadmin}") String accessKey,
            @Value("${storage.minio.secret-key:minioadmin}") String secretKey,
            @Value("${storage.minio.bucket:learnext}") String bucket,
            @Value("${storage.minio.public-base-url:http://localhost:9000/learnext}") String publicBaseUrl
    ) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    public SignedUploadResponse createSignedUploadUrl(String fileName, String contentType, long size) {
        validate(fileName, contentType, size);
        String objectKey = "uploads/" + UUID.randomUUID() + extensionOf(fileName);

        try {
            String uploadUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(15, TimeUnit.MINUTES)
                    .build());

            return SignedUploadResponse.builder()
                    .objectKey(objectKey)
                    .uploadUrl(uploadUrl)
                    .publicUrl(publicBaseUrl.replaceAll("/$", "") + "/" + objectKey)
                    .build();
        } catch (Exception ex) {
            throw new BadRequestException("Cannot create signed upload URL");
        }
    }

    private void validate(String fileName, String contentType, long size) {
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException("File name is required");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Unsupported content type");
        }
        if (size <= 0 || size > MAX_UPLOAD_SIZE) {
            throw new BadRequestException("File size is invalid");
        }

        String extension = extensionOf(fileName);
        if (extension.isBlank()) {
            throw new BadRequestException("File extension is required");
        }
    }

    private String extensionOf(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

}
