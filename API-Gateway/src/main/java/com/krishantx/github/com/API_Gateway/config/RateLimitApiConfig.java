package com.krishantx.github.com.API_Gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RateLimitApiConfig {
  @Value("${rlaas.apikey}")
  private String RLAAS_API_KEY;

  @Bean
  public RestClient rateLimitClient() {
    return RestClient.builder()
        .baseUrl("http://rlaas:8085")
        .defaultHeader("x-api-key", RLAAS_API_KEY)
        .build();
  }
}
