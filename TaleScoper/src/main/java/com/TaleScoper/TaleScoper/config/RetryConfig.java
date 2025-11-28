package com.TaleScoper.TaleScoper.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
    // Empty — @EnableRetry activates Spring Retry infrastructure
}
