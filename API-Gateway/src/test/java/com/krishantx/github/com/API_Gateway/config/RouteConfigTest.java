package com.krishantx.github.com.API_Gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.krishantx.github.com.API_Gateway.entity.Route;

class RouteConfigTest {

  private RouteConfig routeConfig;

  @BeforeEach
  void setUp() {
    routeConfig = new RouteConfig();
    Route product = new Route();
    product.setServiceName("PRODUCT-MICROSERVICE");
    product.setAllowedMethods(Arrays.asList("GET"));
    Map<String, Route> routes = new HashMap<>();
    routes.put("product", product);
    routeConfig.setRoutes(routes);
  }

  @Test
  void init_doesNotFail() {
    routeConfig.init();
  }

  @Test
  void getRoutes_returnsConfiguredMap() {
    assertEquals(1, routeConfig.getRoutes().size());
  }

  @Test
  void findRoute_exactMatch_returnsRoute() {
    Route route = routeConfig.findRoute("product");
    assertNotNull(route);
    assertEquals("PRODUCT-MICROSERVICE", route.getServiceName());
  }

  @Test
  void findRoute_prefixMatch_returnsRoute() {
    Route route = routeConfig.findRoute("product/sku/123");
    assertNotNull(route);
    assertEquals("PRODUCT-MICROSERVICE", route.getServiceName());
  }

  @Test
  void findRoute_trailingSlash_matchesViaPrefix() {
    assertNotNull(routeConfig.findRoute("product/"));
  }

  @Test
  void findRoute_unknownPath_returnsNull() {
    assertNull(routeConfig.findRoute("does-not-exist"));
  }
}