package com.krishantx.github.com.API_Gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.krishantx.github.com.API_Gateway.config.RouteConfig;
import com.krishantx.github.com.API_Gateway.entity.Route;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@Service
public class ServiceDiscovery {
  @Autowired
  private DiscoveryClient discoveryClient;
  @Autowired
  private RouteConfig routeConfig;

  private String getServiceName(String uri) {
    Route route = routeConfig.findRoute(uri.substring(1));
    return route.getServiceName();
  }

  public List<ServiceInstance> getServiceInstances(String uri) {
    String serviceName = getServiceName(uri);
    List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
    return instances;
  }

}
