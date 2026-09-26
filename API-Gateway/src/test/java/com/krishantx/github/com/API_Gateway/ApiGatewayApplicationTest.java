package com.krishantx.github.com.API_Gateway;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class ApiGatewayApplicationTest {

  @Test
  void main_startsApplicationContext() {
    assertDoesNotThrow(() -> ApiGatewayApplication.main(new String[]{
        "--spring.main.web-application-type=none",
        "--eureka.client.enabled=false",
        "--rlaas.apikey=test-key",
        "--server.port=0"
    }));
  }
}