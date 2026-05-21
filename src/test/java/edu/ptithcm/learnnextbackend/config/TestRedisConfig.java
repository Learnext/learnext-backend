package edu.ptithcm.learnnextbackend.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@TestConfiguration
public class TestRedisConfig {
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate redisTemplate =
                Mockito.mock(StringRedisTemplate.class);

        ValueOperations<String, String> valueOperations =
                Mockito.mock(ValueOperations.class);

        Mockito.when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        return redisTemplate;
    }
}
