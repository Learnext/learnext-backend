package edu.ptithcm.learnnextbackend.infrastructures.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Slf4j
public class RedisConnectionLogger {
    private final RedisConnectionFactory connectionFactory;
    private final RedisProperties properties;

    public RedisConnectionLogger(RedisConnectionFactory connectionFactory, RedisProperties properties) {
        this.connectionFactory = connectionFactory;
        this.properties = properties;
    }

    public void logConnection() {
        log.info("Checking Redis connection: {}:{} database={} timeout={}",
                properties.getHost(),
                properties.getPort(),
                properties.getDatabase(),
                properties.getTimeout());

        try (RedisConnection connection = connectionFactory.getConnection()) {
            String response = connection.ping();
            log.info("Redis connected: {}:{} database={} response={}",
                    properties.getHost(),
                    properties.getPort(),
                    properties.getDatabase(),
                    response);
        } catch (Exception e) {
            log.warn("Redis connection failed: {}:{} database={} error={}",
                    properties.getHost(),
                    properties.getPort(),
                    properties.getDatabase(),
                    e.getMessage());
        }
    }
}
