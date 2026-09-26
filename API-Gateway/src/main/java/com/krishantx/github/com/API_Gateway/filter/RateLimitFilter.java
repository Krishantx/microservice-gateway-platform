package com.krishantx.github.com.API_Gateway.filter;

import java.io.IOException;
import java.time.Duration;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import com.krishantx.github.com.API_Gateway.entity.RateLimiterBody;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Order(4)
@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private final RestClient rlaasClient;

  public RateLimitFilter(RestClient rlaasClient) {
    this.rlaasClient = rlaasClient;
  }

  @Override

  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    // return true;
    RateLimiterBody body = new RateLimiterBody(request.getMethod(),
        request.getRequestURI(),
        request.getRemoteAddr());

    long startTime = System.nanoTime();
    log.info("Checking Rate Limit for the URI: \"{}\" and Remote Addr: \"{} \"",
        request.getRequestURI(),
        request.getRemoteAddr());
    HttpStatusCode statusCode = rlaasClient.post()
        .uri("/check")
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .exchange((rlaasRequest, rlaasResponse) -> rlaasResponse.getStatusCode());
    long durationMs = Duration.ofNanos(System.nanoTime() - startTime).toMillis();

    log.info("RLaaS returned Response Code {} for URI {} in time {}", statusCode, request.getRequestURI(), durationMs);

    if (statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentLength(0);
      response.flushBuffer();
      return;
    }

    if (statusCode.is2xxSuccessful()) {
      filterChain.doFilter(request, response);
      return;
    }

    log.warn("RLaaS returned an unexpected response code for URI {} in time {}", request.getRequestURI(), durationMs);
    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    response.setContentLength(0);
    response.flushBuffer();
    return;
  }
}
