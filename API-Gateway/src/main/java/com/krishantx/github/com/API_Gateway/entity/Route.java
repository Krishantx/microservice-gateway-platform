package com.krishantx.github.com.API_Gateway.entity;

import java.util.List;

public class Route {
  private String serviceName;
  private List<String> allowedMethods;
  private List<String> requiredHeaders;
  private boolean authRequired;

  public List<String> getAllowedMethods() {
    return allowedMethods;
  }

  public void setAllowedMethods(List<String> allowedMethods) {
    this.allowedMethods = allowedMethods;
  }

  public List<String> getRequiredHeaders() {
    return requiredHeaders;
  }

  public void setRequiredHeaders(List<String> requiredHeaders) {
    this.requiredHeaders = requiredHeaders;
  }

  public boolean isAuthRequired() {
    return authRequired;
  }

  public void setAuthRequired(boolean authRequired) {
    this.authRequired = authRequired;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Route{");
    sb.append("targetUrl=").append(serviceName);
    sb.append(", allowedMethods=").append(allowedMethods);
    sb.append(", requiredHeaders=").append(requiredHeaders);
    sb.append(", authRequired=").append(authRequired);
    sb.append('}');
    return sb.toString();
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

}
