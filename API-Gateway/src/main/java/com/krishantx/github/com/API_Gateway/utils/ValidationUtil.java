package com.krishantx.github.com.API_Gateway.utils;

import java.util.Enumeration;
import java.util.List;

import com.krishantx.github.com.API_Gateway.entity.Route;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ValidationUtil {

  public boolean endpointExists(Route route, HttpServletRequest request, HttpServletResponse response) {
    if (route == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return false;
    }
    return true;
  }

  public boolean isMethodSupported(Route route, HttpServletRequest request, HttpServletResponse response) {
    String incomingMethod = request.getMethod();
    if (!route.getAllowedMethods().contains(incomingMethod)) {
      response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      return false;
    }
    return true;
  }

  public boolean hasRequiredHeaders(Route route, HttpServletRequest request, HttpServletResponse response) {
    List<String> requiredHeaders = route.getRequiredHeaders();
    if (requiredHeaders == null)
      return true;
    int matchedHeadersCount = 0;
    int requiredHeadersCount = requiredHeaders.size();
    Enumeration<String> incomingHeaders = request.getHeaderNames();
    while (incomingHeaders.hasMoreElements()) {
      String currHeader = incomingHeaders.nextElement();
      if (requiredHeaders.contains(currHeader)) {
        matchedHeadersCount++;
      }
      if (matchedHeadersCount == requiredHeadersCount) {
        break;
      }
    }
    if (matchedHeadersCount != requiredHeadersCount) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return false;
    }
    return true;
  }

}
