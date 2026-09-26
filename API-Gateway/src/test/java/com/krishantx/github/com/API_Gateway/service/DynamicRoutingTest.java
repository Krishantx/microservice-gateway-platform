package com.krishantx.github.com.API_Gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

class DynamicRoutingTest {

  private RestClient.Builder builder;
  private MockRestServiceServer server;
  private DynamicRouting routing;
  private boolean expectRequest = true;

  @BeforeEach
  void setUp() {
    builder = RestClient.builder().baseUrl("http://rlaas:8085");
    server = MockRestServiceServer.bindTo(builder).build();
    routing = new DynamicRouting(builder);
    expectRequest = true;
  }

  @AfterEach
  void tearDown() {
    if (expectRequest) {
      server.verify();
    }
  }

  private ServiceInstance instance(String uri) {
    ServiceInstance instance = mock(ServiceInstance.class);
    when(instance.getUri()).thenReturn(URI.create(uri));
    return instance;
  }

  private List<ServiceInstance> oneInstance() {
    return Collections.singletonList(instance("http://svc:8080"));
  }

  private MockHttpServletRequest getRequest(String path) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
    request.setAttribute("correlationId", "corr-1");
    return request;
  }

  private void jsonOk() {
    server.expect(requestTo("http://svc:8080/product"))
        .andRespond(withSuccess("{\"result\":\"ok\"}", MediaType.APPLICATION_JSON));
  }

  @Test
  void requestInstances_emptyInstances_returns503() {
    expectRequest = false;
    ResponseEntity<?> response = routing.requestInstances(Collections.emptyList(),
        new MockHttpServletRequest("GET", "/product"));
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
  }

  @Test
  void requestInstances_validGet_forwardsToInstanceAndReturnsBody() {
    jsonOk();

    ResponseEntity<?> response = routing.requestInstances(oneInstance(), getRequest("/product"));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Map.of("result", "ok"), response.getBody());
  }

  @Test
  void requestInstances_trailingSlashUri_isNormalizedBeforeForwarding() {
    jsonOk();

    ResponseEntity<?> response = routing.requestInstances(oneInstance(), getRequest("/product/"));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Map.of("result", "ok"), response.getBody());
  }

  @Test
  void requestInstances_withContentTypeCorrelationIdAndBody_forwardsAllMetadata() {
    server.expect(requestTo("http://svc:8080/orders"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header("Content-Type", "application/json"))
        .andExpect(header("X-CORELATION-ID", "corr-42"))
        .andExpect(content().bytes("{}".getBytes(StandardCharsets.UTF_8)))
        .andRespond(withSuccess("{\"result\":\"created\"}", MediaType.APPLICATION_JSON));

    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/orders");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent("{}".getBytes(StandardCharsets.UTF_8));
    request.setAttribute("correlationId", "corr-42");

    ResponseEntity<?> response = routing.requestInstances(oneInstance(), request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Map.of("result", "created"), response.getBody());
  }

  @Test
  void requestInstances_emptyCorrelationId_doesNotForwardHeader() {
    server.expect(requestTo("http://svc:8080/product"))
        .andExpect(headerDoesNotExist("X-CORELATION-ID"))
        .andRespond(withSuccess("{\"result\":\"ok\"}", MediaType.APPLICATION_JSON));

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    request.setAttribute("correlationId", "");

    ResponseEntity<?> response = routing.requestInstances(oneInstance(), request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void requestInstances_missingCorrelationId_propagatesAs500() {
    expectRequest = false;
    ResponseEntity<?> response = routing.requestInstances(oneInstance(),
        new MockHttpServletRequest("GET", "/product"));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void requestInstances_upstreamServerError_returns500() {
    server.expect(requestTo("http://svc:8080/product"))
        .andRespond(withServerError());

    ResponseEntity<?> response = routing.requestInstances(oneInstance(), getRequest("/product"));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  void requestInstances_whenInputStreamThrows_stillForwardsRequest() {
    server.expect(requestTo("http://svc:8080/boom"))
        .andRespond(withSuccess("{\"result\":\"ok\"}", MediaType.APPLICATION_JSON));

    HttpServletRequest request = new HttpServletRequestWrapper(
        new MockHttpServletRequest("GET", "/boom")) {
      @Override
      public ServletInputStream getInputStream() throws IOException {
        throw new IOException("stream exploded");
      }
    };
    request.setAttribute("correlationId", "corr-1");

    ResponseEntity<?> response = routing.requestInstances(oneInstance(), request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Map.of("result", "ok"), response.getBody());
  }
}