package com.krishantx.github.com.API_Gateway.entity;

public class RateLimiterBody {
    private String method;
    private String endpoint;
    private String identifier;

    public RateLimiterBody(String method, String endpoint, String identifier) {
        this.method = method;
        this.endpoint = endpoint;
        this.identifier = identifier;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    
}
