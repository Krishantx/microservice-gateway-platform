package com.krishantx.github.com.API_Gateway.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(1)
@Slf4j
public class CorelationIdFilter extends OncePerRequestFilter {
  private final String HEADER = "X-Corelation-Id";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String corelationId = request.getHeader(HEADER);
    if (corelationId == null) {
      corelationId = UUID.randomUUID().toString();
    }
    MDC.put("correlationId", corelationId);

    response.setHeader(HEADER, corelationId);
    request.setAttribute("correlationId", corelationId);
    try {
      doFilter(request, response, filterChain);
    } finally {
      MDC.remove("correlationId");
    }
  }
}
