package edu.ptithcm.learnnextbackend.infrastructures.minio;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MinioConnectionLogger {
    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioConnectionLogger(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public void logConnection() {
        log.info("Checking MinIO connection: endpoint={} bucket={} timeout={}",
                properties.getEndpoint(),
                properties.getBucket(),
                properties.getTimeout());

        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(properties.getBucket())
                    .build());

            log.info("MinIO connected: endpoint={} bucket={} bucketExists={}",
                    properties.getEndpoint(),
                    properties.getBucket(),
                        bucketExists);
        } catch (Exception e) {
            log.warn("MinIO connection failed: endpoint={} bucket={} error={}",
                    properties.getEndpoint(),
                    properties.getBucket(),
                    e.getMessage());
        }
    }
}
