package com.krishantx.github.com.API_Gateway.filter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorelationIdFilterTest {

  private CorelationIdFilter filter;

  @BeforeEach
  void setUp() {
    MDC.clear();
    filter = new CorelationIdFilter();
  }

  @Test
  void missingHeader_generatesUuidOnResponseAndRequest() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    String header = response.getHeader("X-Corelation-Id");
    assertNotNull(header);
    assertDoesNotThrow(() -> UUID.fromString(header));
    assertEquals(header, request.getAttribute("correlationId"));
    assertNotNull(chain.getRequest());
  }

  @Test
  void providedHeader_isReusedInsteadOfRegenerated() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    request.addHeader("X-Corelation-Id", "trace-123");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals("trace-123", response.getHeader("X-Corelation-Id"));
    assertEquals("trace-123", request.getAttribute("correlationId"));
    assertNotNull(chain.getRequest());
  }

  @Test
  void mdcIsClearedAfterFilterRuns() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertNull(MDC.get("correlationId"));
  }
}