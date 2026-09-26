package com.krishantx.github.com.API_Gateway.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.krishantx.github.com.API_Gateway.config.RouteConfig;
import com.krishantx.github.com.API_Gateway.entity.Route;
import com.krishantx.github.com.API_Gateway.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Order(3)
@Component
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

  // Validate user using a Identity Provider Microservice
  @Autowired
  private RouteConfig routeConfig;
  @Autowired
  private JwtService jwtService;
  private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    // Convert JWT token to User Using an Identity Provider Service.
    Route route = routeConfig.findRoute(request.getRequestURI().substring(1));

    if (!route.isAuthRequired()) {
      securityLogger.info("No Authentication Required");
      doFilter(request, response, filterChain);

      return;
    }
    String token = request.getHeader("Authorization");
    if (token == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    if (token.length() < 8) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    token = token.substring(7);
    String username = new String();
    try {
      username = jwtService.validateToken(token);
    } catch (JwtException exception) {
      securityLogger.info("Expired JWT Provided : " + exception);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    if (username == null) {
      securityLogger.info("Unable to validate the user returning 401");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    request.setAttribute("username", username);
    log.info("\"{}\" is validated");
    filterChain.doFilter(request, response);
  }
}
