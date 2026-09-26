package com.krishantx.github.com.API_Gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

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
import com.krishantx.github.com.API_Gateway.service.JwtService;

class AuthenticationFilterTest {

  @Mock
  private RouteConfig routeConfig;

  private final JwtService jwtService = new JwtService();

  private AuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    filter = new AuthenticationFilter();
    ReflectionTestUtils.setField(filter, "routeConfig", routeConfig);
    ReflectionTestUtils.setField(filter, "jwtService", jwtService);
  }

  private Route route(boolean authRequired) {
    Route route = new Route();
    route.setServiceName("SAMPLE-MICROSERVICE");
    route.setAllowedMethods(Collections.singletonList("GET"));
    route.setAuthRequired(authRequired);
    return route;
  }

  @Test
  void authNotRequired_passesThroughWithoutToken() throws Exception {
    when(routeConfig.findRoute("product")).thenReturn(route(false));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(200, response.getStatus());
    assertNotNull(chain.getRequest());
  }

  @Test
  void validToken_passesThroughAndSetsUsernameAttribute() throws Exception {
    when(routeConfig.findRoute("secure")).thenReturn(route(true));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secure");
    request.addHeader("Authorization", "Bearer " + JwtService.generateToken("krishant", 300000));
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(200, response.getStatus());
    assertEquals("krishant", request.getAttribute("username"));
    assertNotNull(chain.getRequest());
  }

  @Test
  void expiredToken_returns401AndSkipsChain() throws Exception {
    when(routeConfig.findRoute("secure")).thenReturn(route(true));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secure");
    request.addHeader("Authorization", "Bearer " + JwtService.generateToken("krishant", -1000));
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    try {
      filter.doFilter(request, response, chain);
    } catch (RuntimeException e) {
      fail("Expected 401 for an expired token, but the filter threw: " + e);
    }

    assertEquals(401, response.getStatus());
    assertEquals(null, chain.getRequest());
  }

  @Test
  void missingAuthorizationHeader_returns401() throws Exception {
    when(routeConfig.findRoute("secure")).thenReturn(route(true));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secure");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    try {
      filter.doFilter(request, response, chain);
    } catch (RuntimeException e) {
      fail("Expected 401 for a missing Authorization header, but the filter threw: " + e);
    }

    assertEquals(401, response.getStatus());
  }

  @Test
  void malformedToken_returns401() throws Exception {
    when(routeConfig.findRoute("secure")).thenReturn(route(true));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secure");
    request.addHeader("Authorization", "Bearer not-a-valid-jwt");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    try {
      filter.doFilter(request, response, chain);
    } catch (RuntimeException e) {
      fail("Expected 401 for a malformed token, but the filter threw: " + e);
    }

    assertEquals(401, response.getStatus());
  }

  @Test
  void emptyBearerValue_returns401AndSkipsChain() throws Exception {
    when(routeConfig.findRoute("secure")).thenReturn(route(true));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secure");
    request.addHeader("Authorization", "Bearer ");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    try {
      filter.doFilter(request, response, chain);
    } catch (RuntimeException e) {
      fail("Expected 401 for an empty Bearer value, but the filter threw: " + e);
    }

    assertEquals(401, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void shortAuthorizationHeader_returns401AndSkipsChain() throws Exception {
    when(routeConfig.findRoute("secure")).thenReturn(route(true));
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secure");
    request.addHeader("Authorization", "abc");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    try {
      filter.doFilter(request, response, chain);
    } catch (RuntimeException e) {
      fail("Expected 401 for a malformed Authorization header, but the filter threw: " + e);
    }

    assertEquals(401, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void nullUsernameFromValidator_returns401AndSkipsChain() throws Exception {
    when(routeConfig.findRoute("secure")).thenReturn(route(true));
    JwtService stubJwt = mock(JwtService.class);
    when(stubJwt.validateToken(anyString())).thenReturn(null);
    ReflectionTestUtils.setField(filter, "jwtService", stubJwt);

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/secure");
    request.addHeader("Authorization", "Bearer " + JwtService.generateToken("anybody", 300000));
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertEquals(401, response.getStatus());
    assertNull(chain.getRequest());
  }
}