package com.github.krishantx.RLaaS.Model;

public class AddEndpointDTO {
    private int rateLimit;
    private String endpoint;
    public int getRateLimit() {
        return rateLimit;
    }
    public void setRateLimit(int rateLimit) {
        this.rateLimit = rateLimit;
    }
    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    
    
}
