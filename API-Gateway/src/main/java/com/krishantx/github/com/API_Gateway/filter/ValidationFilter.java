package com.krishantx.github.com.API_Gateway.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.krishantx.github.com.API_Gateway.config.RouteConfig;
import com.krishantx.github.com.API_Gateway.entity.Route;
import com.krishantx.github.com.API_Gateway.utils.ValidationUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Order(2)
@Slf4j
@Component
public class ValidationFilter extends OncePerRequestFilter {
  @Autowired
  private RouteConfig routeConfig;

  private final ValidationUtil validationUtil = new ValidationUtil();

  @Override

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    log.info("Validation Filter Started");
    String incomingRequest = request.getRequestURI().substring(1);
    if (incomingRequest.endsWith("/")) {
      incomingRequest = incomingRequest.substring(0, incomingRequest.length() - 1);
    }
    Route route = routeConfig.findRoute(incomingRequest);
    // Check if the endpoint exists, If not return 404

    if (!validationUtil.endpointExists(route, request, response)) {
      log.info("The Endpoint \"{}\" does not exist", incomingRequest);
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    // Check if the endpoint supports that method
    if (!validationUtil.isMethodSupported(route, request, response)) {
      log.info("HTTP Method \"{}\" not supported for endpoint {}",
          request.getMethod(),
          incomingRequest);
      response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      return;
    }

    // Check if the request contains the required headers
    if (!validationUtil.hasRequiredHeaders(route, request, response)) {
      log.info("Request does not contain the required headers");
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required headers");
      return;
    }
    log.info("Validation Checks passed");
    // If all checks pass -> move to the next filter chain
    filterChain.doFilter(request, response);
  }
}
