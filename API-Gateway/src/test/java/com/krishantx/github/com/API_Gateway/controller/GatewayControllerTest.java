package com.krishantx.github.com.API_Gateway.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.krishantx.github.com.API_Gateway.service.DynamicRouting;
import com.krishantx.github.com.API_Gateway.service.ServiceDiscovery;

import jakarta.servlet.http.HttpServletRequest;

class GatewayControllerTest {

  private DynamicRouting dynamicRouting;
  private ServiceDiscovery serviceDiscovery;
  private GatewayController controller;

  @BeforeEach
  void setUp() {
    dynamicRouting = mock(DynamicRouting.class);
    serviceDiscovery = mock(ServiceDiscovery.class);
    controller = new GatewayController();
    ReflectionTestUtils.setField(controller, "dynamicRouting", dynamicRouting);
    ReflectionTestUtils.setField(controller, "serviceDiscovery", serviceDiscovery);
  }

  @Test
  void gatewayController_returnsRoutingResponse() {
    when(serviceDiscovery.getServiceInstances("/product")).thenReturn(Collections.emptyList());
    ResponseEntity<?> expected = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    doReturn(expected).when(dynamicRouting).requestInstances(
        eq(Collections.emptyList()), any(HttpServletRequest.class));

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
    ResponseEntity<?> result = controller.gatewayController(request);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
    verify(serviceDiscovery).getServiceInstances("/product");
  }

  @Test
  void gatewayController_withTrailingSlash_stripsItBeforeDiscovery() {
    when(serviceDiscovery.getServiceInstances("/product")).thenReturn(Collections.emptyList());
    ResponseEntity<?> expected = ResponseEntity.ok("done");
    doReturn(expected).when(dynamicRouting).requestInstances(
        eq(Collections.emptyList()), any(HttpServletRequest.class));

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product/");
    ResponseEntity<?> result = controller.gatewayController(request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    verify(serviceDiscovery).getServiceInstances("/product");
  }
}