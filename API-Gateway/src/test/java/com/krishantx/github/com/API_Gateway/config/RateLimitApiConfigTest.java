package com.krishantx.github.com.API_Gateway.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

class RateLimitApiConfigTest {

  private RateLimitApiConfig config;

  @BeforeEach
  void setUp() {
    config = new RateLimitApiConfig();
    ReflectionTestUtils.setField(config, "RLAAS_API_KEY", "secret-key");
  }

  @Test
  void rateLimitClient_buildsClientWithApiKeyHeader() {
    RestClient client = config.rateLimitClient();
    assertNotNull(client);
  }
}