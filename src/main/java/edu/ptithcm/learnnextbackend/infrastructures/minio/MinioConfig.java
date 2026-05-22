package edu.ptithcm.learnnextbackend.infrastructures.minio;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@Slf4j
public class MinioConfig {

    @Bean
    @ConditionalOnProperty(prefix = "minio", name = "endpoint")
    public MinioClient minioClient(MinioProperties properties) {
        log.info("MinIO client configured: endpoint={} bucket={}",
                properties.getEndpoint(),
                properties.getBucket());

        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .httpClient(new OkHttpClient.Builder()
                        .connectTimeout(properties.getTimeout())
                        .readTimeout(properties.getTimeout())
                        .writeTimeout(properties.getTimeout())
                        .build());

        if (StringUtils.hasText(properties.getRegion())) {
            builder.region(properties.getRegion());
        }

        return builder.build();
    }

    @Bean(initMethod = "logConnection")
    @ConditionalOnProperty(prefix = "minio", name = "endpoint")
    public MinioConnectionLogger minioConnectionLogger(MinioClient minioClient, MinioProperties properties) {
        return new MinioConnectionLogger(minioClient, properties);
    }
}
