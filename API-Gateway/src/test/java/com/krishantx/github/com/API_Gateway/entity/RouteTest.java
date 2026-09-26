package com.krishantx.github.com.API_Gateway.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class RouteTest {

  @Test
  void gettersAndSetters_roundTrip() {
    Route route = new Route();
    route.setServiceName("SVC");
    route.setAllowedMethods(Arrays.asList("GET", "POST"));
    route.setRequiredHeaders(Collections.singletonList("x-api-key"));
    route.setAuthRequired(true);

    assertEquals("SVC", route.getServiceName());
    assertEquals(Arrays.asList("GET", "POST"), route.getAllowedMethods());
    assertEquals(Collections.singletonList("x-api-key"), route.getRequiredHeaders());
    assertTrue(route.isAuthRequired());
  }

  @Test
  void newRoute_defaultsToNoAuth() {
    assertFalse(new Route().isAuthRequired());
  }

  @Test
  void toString_containsAllFields() {
    Route route = new Route();
    route.setServiceName("SVC");
    route.setAllowedMethods(Collections.singletonList("GET"));
    route.setRequiredHeaders(Collections.singletonList("x-api-key"));
    route.setAuthRequired(true);

    String s = route.toString();
    assertTrue(s.contains("Route{"));
    assertTrue(s.contains("SVC"));
    assertTrue(s.contains("GET"));
    assertTrue(s.contains("x-api-key"));
    assertTrue(s.contains("authRequired=true"));
  }
}