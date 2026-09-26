package com.krishantx.github.com.API_Gateway.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.krishantx.github.com.API_Gateway.entity.Route;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class ValidationUtilTest {

  private ValidationUtil validationUtil;
  private HttpServletResponse response;

  @BeforeEach
  void setUp() {
    validationUtil = new ValidationUtil();
    response = new MockHttpServletResponse();
  }

  private Route route(String method, String header) {
    Route route = new Route();
    route.setServiceName("SAMPLE-MICROSERVICE");
    route.setAllowedMethods(method == null ? Collections.emptyList() : Arrays.asList(method));
    route.setRequiredHeaders(header == null ? null : Arrays.asList(header));
    return route;
  }

  @Test
  void endpointExists_nullRoute_returns404AndFalse() {
    HttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    boolean result = validationUtil.endpointExists(null, request, response);
    assertFalse(result);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }

  @Test
  void endpointExists_existingRoute_returnsTrueWithoutChangingStatus() {
    HttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    boolean result = validationUtil.endpointExists(route("GET", null), request, response);
    assertTrue(result);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void isMethodSupported_allowedMethod_returnsTrue() {
    HttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    boolean result = validationUtil.isMethodSupported(route("GET", null), request, response);
    assertTrue(result);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void isMethodSupported_disallowedMethod_returns405() {
    HttpServletRequest request = new MockHttpServletRequest("POST", "/product");
    boolean result = validationUtil.isMethodSupported(route("GET", null), request, response);
    assertFalse(result);
    assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
  }

  @Test
  void hasRequiredHeaders_nullRequiredHeaders_returnsTrue() {
    HttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    boolean result = validationUtil.hasRequiredHeaders(route("GET", null), request, response);
    assertTrue(result);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void hasRequiredHeaders_allHeadersPresent_returnsTrue() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/profile");
    request.addHeader("x-api-key", "secret");
    request.addHeader("X-Random-Header", "ignored");
    boolean result = validationUtil.hasRequiredHeaders(route("GET", "x-api-key"), request, response);
    assertTrue(result);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void hasRequiredHeaders_missingHeader_returns400() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/profile");
    request.addHeader("X-Random-Header", "not-the-one");
    boolean result = validationUtil.hasRequiredHeaders(route("GET", "x-api-key"), request, response);
    assertFalse(result);
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  @Test
  void hasRequiredHeaders_multipleRequiredHeaders_allPresent_returnsTrue() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v2");
    request.addHeader("x-api-key", "k1");
    request.addHeader("Content-Type", "application/json");
    Route route = new Route();
    route.setRequiredHeaders(Arrays.asList("x-api-key", "Content-Type"));

    boolean result = validationUtil.hasRequiredHeaders(route, request, response);

    assertTrue(result);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void hasRequiredHeaders_multipleRequiredHeaders_oneMissing_returns400() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v2");
    request.addHeader("x-api-key", "k1");
    Route route = new Route();
    route.setRequiredHeaders(Arrays.asList("x-api-key", "Content-Type"));

    boolean result = validationUtil.hasRequiredHeaders(route, request, response);

    assertFalse(result);
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }
}