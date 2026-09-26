package com.krishantx.github.com.API_Gateway.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RateLimiterBodyTest {

  @Test
  void constructorAndAccessors_roundTrip() {
    RateLimiterBody body = new RateLimiterBody("GET", "/product", "127.0.0.1");
    assertEquals("GET", body.getMethod());
    assertEquals("/product", body.getEndpoint());
    assertEquals("127.0.0.1", body.getIdentifier());

    body.setMethod("POST");
    body.setEndpoint("/orders");
    body.setIdentifier("10.0.0.1");

    assertEquals("POST", body.getMethod());
    assertEquals("/orders", body.getEndpoint());
    assertEquals("10.0.0.1", body.getIdentifier());
  }
}