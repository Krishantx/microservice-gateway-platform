package com.krishantx.github.com.API_Gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class RateLimitFilterTest {

  private MockRestServiceServer server;
  private RateLimitFilter filter;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://rlaas:8085");
    server = MockRestServiceServer.bindTo(builder).build();
    filter = new RateLimitFilter(builder.build());
  }

  @AfterEach
  void tearDown() {
    server.verify();
  }

  @Test
  void rlaasReturns429_sets429AndSkipsChain() throws Exception {
    server.expect(anything()).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(429, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void rlaasReturns2xx_passesThroughToChain() throws Exception {
    server.expect(anything()).andRespond(withStatus(HttpStatus.OK));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(200, response.getStatus());
    assertNotNull(chain.getRequest());
  }

  @Test
  void rlaasReturnsOther5xx_sets503AndSkipsChain() throws Exception {
    server.expect(anything()).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(503, response.getStatus());
    assertNull(chain.getRequest());
  }
}