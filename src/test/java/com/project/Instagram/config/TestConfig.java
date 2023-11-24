package com.project.Instagram.config;

import com.project.Instagram.global.jwt.RefreshTokenRedisRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    @Bean
    public RefreshTokenRedisRepository refreshTokenRedisRepository() {
        return mock(RefreshTokenRedisRepository.class);
    }
}
