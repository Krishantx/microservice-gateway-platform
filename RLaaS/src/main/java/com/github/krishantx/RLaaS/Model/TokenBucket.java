package com.github.krishantx.RLaaS.Model;

public class TokenBucket {
    private float tokens;
    private int rateLimit;
    private long timestamp;

    public TokenBucket(float tokens, int rateLimit, long timestamp) {
        this.tokens = tokens;
        this.rateLimit = rateLimit;
        this.timestamp = timestamp;
    }

    public TokenBucket(){}

    public int getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(int rateLimit) {
        this.rateLimit = rateLimit;
    }

    public TokenBucket(float tokens, long timestamp) {
        this.tokens = tokens;
        this.timestamp = timestamp;
    }

    public float getTokens() {
        return tokens;
    }

    public void setTokens(float tokens) {
        this.tokens = tokens;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Tokens : " + tokens + " \nTimestamp " + timestamp;
    }
}
