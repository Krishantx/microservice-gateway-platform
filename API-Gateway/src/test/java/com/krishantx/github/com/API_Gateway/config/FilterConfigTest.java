package com.krishantx.github.com.API_Gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.krishantx.github.com.API_Gateway.filter.AuthenticationFilter;
import com.krishantx.github.com.API_Gateway.filter.CorelationIdFilter;
import com.krishantx.github.com.API_Gateway.filter.ValidationFilter;

class FilterConfigTest {

  private FilterConfig filterConfig;

  @BeforeEach
  void setUp() {
    filterConfig = new FilterConfig();
  }

  @Test
  void validationFilterRegistration_isOrderedSecondAndCoversAllPaths() {
    FilterRegistrationBean<ValidationFilter> registration =
        filterConfig.validationFilterRegistration(new ValidationFilter());
    assertEquals(2, registration.getOrder());
    assertTrue(registration.getUrlPatterns().contains("/*"));
    assertFalse(registration.getUrlPatterns().isEmpty());
  }

  @Test
  void authenticationFilterRegistration_isOrderedThirdAndCoversAllPaths() {
    FilterRegistrationBean<AuthenticationFilter> registration =
        filterConfig.authenticationFilterRegisteration1(new AuthenticationFilter());
    assertEquals(3, registration.getOrder());
    assertTrue(registration.getUrlPatterns().contains("/*"));
    assertFalse(registration.getUrlPatterns().isEmpty());
  }

  @Test
  void corelationIdFilterRegistration_isOrderedFirstAndCoversAllPaths() {
    FilterRegistrationBean<CorelationIdFilter> registration =
        filterConfig.corelationIdFilterRegistration(new CorelationIdFilter());
    assertEquals(1, registration.getOrder());
    assertTrue(registration.getUrlPatterns().contains("/*"));
    assertFalse(registration.getUrlPatterns().isEmpty());
  }
}