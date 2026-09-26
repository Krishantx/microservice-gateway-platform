package com.krishantx.github.com.API_Gateway.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.krishantx.github.com.API_Gateway.filter.AuthenticationFilter;
import com.krishantx.github.com.API_Gateway.filter.CorelationIdFilter;
import com.krishantx.github.com.API_Gateway.filter.RateLimitFilter;
import com.krishantx.github.com.API_Gateway.filter.ValidationFilter;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<ValidationFilter> validationFilterRegistration(
      ValidationFilter filter) {

    FilterRegistrationBean<ValidationFilter> registration = new FilterRegistrationBean<>();

    registration.setFilter(filter);
    registration.addUrlPatterns("/*");
    registration.setOrder(2);

    return registration;
  }

  @Bean
  public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegisteration1(
      AuthenticationFilter filter) {
    FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>();

    registration.setFilter(filter);
    registration.addUrlPatterns("/*");
    registration.setOrder(3);
    return registration;
  }

  // @Bean
  // public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegisteration(
  // RateLimitFilter filter) {
  // FilterRegistrationBean<RateLimitFilter> registration = new
  // FilterRegistrationBean<>();

  // registration.setFilter(filter);
  // registration.addUrlPatterns("/*");
  // registration.setOrder(4);

  // return registration;
  // }

  @Bean
  public FilterRegistrationBean<CorelationIdFilter> corelationIdFilterRegistration(
      CorelationIdFilter filter) {
    FilterRegistrationBean<CorelationIdFilter> registration = new FilterRegistrationBean<>();

    registration.setFilter(filter);
    registration.addUrlPatterns("/*");
    registration.setOrder(1);

    return registration;
  }
}
