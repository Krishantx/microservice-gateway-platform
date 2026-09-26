package com.krishantx.github.com.API_Gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.krishantx.github.com.API_Gateway.service.JwtService;
import com.krishantx.github.com.API_Gateway.service.ServiceDiscovery;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "eureka.client.enabled=false")
class GatewayRoutingIntegrationTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @MockitoBean
  private ServiceDiscovery serviceDiscovery;

  @BeforeEach
  void setUp() {
    when(serviceDiscovery.getServiceInstances(anyString()))
        .thenReturn(Collections.emptyList());
    RateLimitClientTestConfig.server.reset();
    // The RateLimitFilter re-runs on the error re-dispatch (Tomcat forwards 5xx
    // responses through the filter chain), so a valid request triggers two /check calls.
    RateLimitClientTestConfig.server.expect(anything())
        .andRespond(withStatus(HttpStatus.OK));
    RateLimitClientTestConfig.server.expect(anything())
        .andRespond(withStatus(HttpStatus.OK));
  }

  @AfterEach
  void tearDown() {
    RateLimitClientTestConfig.server.reset();
  }

  @Test
  void unknownEndpoint_returns404() {
    ResponseEntity<String> response = restTemplate.getForEntity("/does-not-exist", String.class);
    assertEquals(404, response.getStatusCode().value());
    assertNotNull(response.getHeaders().getFirst("X-Corelation-Id"));
  }

  @Test
  void disallowedMethod_returns405() {
    ResponseEntity<String> response = restTemplate.exchange("/product", HttpMethod.POST, null,
        String.class);
    assertEquals(405, response.getStatusCode().value());
    assertNotNull(response.getHeaders().getFirst("X-Corelation-Id"));
  }

  @Test
  void missingRequiredHeader_returns400() {
    ResponseEntity<String> response = restTemplate.getForEntity("/profile", String.class);
    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getHeaders().getFirst("X-Corelation-Id"));
  }

  @Test
  void validRequest_passesThroughFiltersAndReturns503FromEmptyRegistry() {
    ResponseEntity<String> response = restTemplate.getForEntity("/product", String.class);
    assertEquals(503, response.getStatusCode().value(), "body=" + response.getBody());
    assertNotNull(response.getHeaders().getFirst("X-Corelation-Id"));
  }

  @Test
  void validRequestWithRequiredHeaderAndCorrelationId_passesThrough() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("x-api-key", "test-key");
    headers.set("X-Corelation-Id", "trace-injected");
    headers.set("Authorization", "Bearer " + JwtService.generateToken("testuser"));
    ResponseEntity<String> response = restTemplate.exchange("/profile", HttpMethod.GET,
        new HttpEntity<>(headers), String.class);
    assertEquals(503, response.getStatusCode().value());
    assertEquals("trace-injected", response.getHeaders().getFirst("X-Corelation-Id"));
  }

  @TestConfiguration
  static class RateLimitClientTestConfig {

    static MockRestServiceServer server;

    @Bean
    @Primary
    RestClient rateLimitClientMock() {
      RestClient.Builder builder = RestClient.builder().baseUrl("http://rlaas:8085");
      server = MockRestServiceServer.bindTo(builder).build();
      return builder.build();
    }
  }
}