package com.krishantx.github.com.API_Gateway.service;

import java.util.List;
import java.util.Random;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DynamicRouting {
  private final RestClient restClient;

  public DynamicRouting(RestClient.Builder builder) {
    this.restClient = builder.build();
  }

  public ResponseEntity<?> requestInstances(List<ServiceInstance> instances, HttpServletRequest request) {
    if (instances.size() == 0) {
      return ResponseEntity.status(503).build();
    }
    ServiceInstance randomInstance = instances.get(new Random().nextInt(instances.size()));
    String incomingRequest = request.getRequestURI();
    if (incomingRequest.endsWith("/")) {
      incomingRequest = incomingRequest.substring(0, incomingRequest.length() - 1);
    }
    String requestURL = randomInstance.getUri() + "/" + incomingRequest;

    byte[] requestBody = null;

    try {
      requestBody = request.getInputStream().readAllBytes();
    } catch (Exception e) {
      System.out.println(e);
    }

    try {
      RestClient.RequestBodySpec spec = this.restClient
          .method(HttpMethod.valueOf(request.getMethod()))
          .uri(requestURL);

      String contentType = request.getContentType();

      if (contentType != null) {
        spec.contentType(MediaType.parseMediaType(contentType));
      }

      Object corelationId = request.getAttribute("correlationId");

      if (corelationId == null)
        return ResponseEntity.internalServerError().build();

      if (corelationId.toString().length() > 0)
        spec.header("X-CORELATION-ID", corelationId.toString());

      if (requestBody != null && requestBody.length > 0) {
        spec.body(requestBody);
      }

      Object res = spec.retrieve().body(Object.class);
      return ResponseEntity.ok(res);
    } catch (

    Exception e) {
      log.error("Exception encountered: {}", e);
      return ResponseEntity.internalServerError().build();
    }

  }
}
