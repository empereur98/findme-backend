package com.dhi.findme_backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
public class TestJpaConfig {
    // This class is used to override the main JpaAuditing configuration
    // with a test-specific one that doesn't require a full security context.
}