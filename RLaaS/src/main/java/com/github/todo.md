# Rate Limiting as a Service TODO


## Phase 1 - Core API
- [x] Create /check endpoint
- [x] Create request DTO
- [x] Create response DTO
- [x] Add validation
Create a single client bare bone version
Flow: A client recieves a HTTP request. It does not process it right away. The request is forwarded to the rate limiter with the following body.
{
    "identifier/ip" : the ip,
    
}

---

## Phase 2 - In-Memory Rate Limiter
- [x] Create RateLimiter interface
- [x] Implement Fixed Window algorithm
- [x] Add expiry logic
- [x] Test with Postman

---

## Phase 3 - Redis
- [x] Install Redis
- [x] Connect Redis to Spring Boot
- [x] Replace HashMap with Redis
- [x] Add TTL support

---

# Phase 4 - Perfecting the Single Tenant Architecture
- [] Add an endpoint based Rate Limit
- [] Implement the TTL logic
- [] Implement @Async db population for endpoints

## Improvements
- [ ] Add Docker
- [ ] Add Grafana
- [ ] Add dashboard