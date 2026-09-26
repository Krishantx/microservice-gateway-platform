package com.github.krishantx.RLaaS.Model.DatabaseModels;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class EndpointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int endpointId;
    
    @ManyToOne
    @Autowired
    private ClientEntity client;
    private String endpoint;
    private int rateLimit;

    @Override
    public String toString() {
        return client + " " + endpoint + " " + rateLimit;
    }
    public EndpointEntity(String endpoint, int rateLimit){
        this.endpoint = endpoint;
        this.rateLimit = rateLimit;
    }
    public EndpointEntity() {}
    public void serEndpointId(int endpointId) {
        this.endpointId = endpointId;
    }
    public int getEndpointId() {
        return this.endpointId;
    }
    public ClientEntity getClient() {
        return client;
    }
    public void setClient(ClientEntity client) {
        this.client = client;
    }
    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    public int getRateLimit() {
        return rateLimit;
    }
    public void setRateLimit(int rateLimit) {
        this.rateLimit = rateLimit;
    }

    
}
