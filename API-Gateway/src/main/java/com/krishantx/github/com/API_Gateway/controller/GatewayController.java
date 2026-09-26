package com.krishantx.github.com.API_Gateway.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krishantx.github.com.API_Gateway.service.DynamicRouting;
import com.krishantx.github.com.API_Gateway.service.ServiceDiscovery;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GatewayController {

  @Autowired
  private DynamicRouting dynamicRouting;

  @Autowired
  private ServiceDiscovery serviceDiscovery;

  @RequestMapping("/**")
  public ResponseEntity<?> gatewayController(HttpServletRequest request) {
    String requestUri = request.getRequestURI();
    if (requestUri.endsWith("/")) {
      requestUri = requestUri.substring(0, requestUri.length() - 1);
    }
    List<ServiceInstance> instances = serviceDiscovery.getServiceInstances(requestUri);
    ResponseEntity<?> response = dynamicRouting.requestInstances(instances, request);
    return response;
  }
}
