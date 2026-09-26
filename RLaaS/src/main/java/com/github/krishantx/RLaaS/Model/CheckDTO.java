package com.github.krishantx.RLaaS.Model;


public class CheckDTO {
    private String identifier;
    private String method;
    private String endpoint;

    
    public CheckDTO(String identifier) { this.identifier = identifier; }

    @Override
    public String toString() {
        return identifier;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
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
}
