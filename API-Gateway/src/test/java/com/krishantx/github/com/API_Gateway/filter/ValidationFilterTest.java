package com.krishantx.github.com.API_Gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import com.krishantx.github.com.API_Gateway.config.RouteConfig;
import com.krishantx.github.com.API_Gateway.entity.Route;

class ValidationFilterTest {

  @Mock
  private RouteConfig routeConfig;

  private ValidationFilter filter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    filter = new ValidationFilter();
    ReflectionTestUtils.setField(filter, "routeConfig", routeConfig);
  }

  private Route route(String method, String header) {
    Route route = new Route();
    route.setServiceName("SAMPLE-MICROSERVICE");
    route.setAllowedMethods(Arrays.asList(method));
    route.setRequiredHeaders(header == null ? null : Arrays.asList(header));
    return route;
  }

  @Test
  void unknownEndpoint_returns404AndSkipsChain() throws Exception {
    when(routeConfig.findRoute("does-not-exist")).thenReturn(null);
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/does-not-exist");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(404, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void disallowedMethod_returns405AndSkipsChain() throws Exception {
    when(routeConfig.findRoute("product")).thenReturn(route("GET", null));
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(405, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void missingRequiredHeader_returns400AndSkipsChain() throws Exception {
    when(routeConfig.findRoute("profile")).thenReturn(route("GET", "x-api-key"));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/profile");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(400, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void validRequest_passesThroughToChain() throws Exception {
    when(routeConfig.findRoute("product")).thenReturn(route("GET", null));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(200, response.getStatus());
    assertNotNull(chain.getRequest());
  }

  @Test
  void validRequestWithTrailingSlash_isNormalizedBeforeRouteLookup() throws Exception {
    when(routeConfig.findRoute("product")).thenReturn(route("GET", null));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product/");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(200, response.getStatus());
    assertNotNull(chain.getRequest());
  }

  @Test
  void requiredHeaderPresent_passesThroughToChain() throws Exception {
    when(routeConfig.findRoute("profile")).thenReturn(route("GET", "x-api-key"));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/profile");
    request.addHeader("x-api-key", "secret");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(200, response.getStatus());
    assertNotNull(chain.getRequest());
  }
}
