package com.krishantx.github.com.API_Gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.util.ReflectionTestUtils;

import com.krishantx.github.com.API_Gateway.config.RouteConfig;
import com.krishantx.github.com.API_Gateway.entity.Route;

class ServiceDiscoveryTest {

  @Mock
  private DiscoveryClient discoveryClient;

  @Mock
  private RouteConfig routeConfig;

  private ServiceDiscovery serviceDiscovery;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    serviceDiscovery = new ServiceDiscovery();
    ReflectionTestUtils.setField(serviceDiscovery, "discoveryClient", discoveryClient);
    ReflectionTestUtils.setField(serviceDiscovery, "routeConfig", routeConfig);
  }

  private Route route(String serviceName) {
    Route route = new Route();
    route.setServiceName(serviceName);
    return route;
  }

  private ServiceInstance instance(String uri) {
    ServiceInstance instance = mock(ServiceInstance.class);
    when(instance.getUri()).thenReturn(URI.create(uri));
    return instance;
  }

  @Test
  void getServiceInstances_knownUri_stripsLeadingSlashAndQueriesDiscovery() {
    when(routeConfig.findRoute("product")).thenReturn(route("SAMPLE-MICROSERVICE"));
    List<ServiceInstance> expected = Collections.singletonList(instance("http://svc:8080"));
    when(discoveryClient.getInstances("SAMPLE-MICROSERVICE")).thenReturn(expected);

    List<ServiceInstance> result = serviceDiscovery.getServiceInstances("/product");

    assertEquals(expected, result);
  }

  @Test
  void getServiceInstances_deepPath_resolvesRouteFromFirstSegment() {
    when(routeConfig.findRoute("product/sku/123")).thenReturn(route("PRODUCT-MICROSERVICE"));
    when(discoveryClient.getInstances("PRODUCT-MICROSERVICE")).thenReturn(Collections.emptyList());

    assertEquals(Collections.emptyList(), serviceDiscovery.getServiceInstances("/product/sku/123"));
  }

  @Test
  void getServiceInstances_unknownUri_throwsNullPointer() {
    when(routeConfig.findRoute("nope")).thenReturn(null);
    assertThrows(NullPointerException.class, () -> serviceDiscovery.getServiceInstances("/nope"));
  }
}