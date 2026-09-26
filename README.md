# Microservice-Architecture

Self-contained microservice demo — an API Gateway with JWT auth + dynamic routing, service discovery, three sample services, and a separate token-bucket rate limiter (RLaaS). Everything runs locally with Docker, one command.

## Architecture

```
                          ┌────────────────────────────────────────────┐
 client ───▶ :8080        │ API Gateway                                │
             ┌───────┐    │  1. Correlation-ID filter                  │
             │       │    │  2. Header validation (x-api-key)          │
             │ 8080  │    │  3. JWT auth (Bearer, HS256) → 401         │
             │       │    │  4. RLaaS rate-limit check → 429/503       │
             └───┬───┘    │  5. Dynamic routing via Eureka             │
                 │        └────────────────────────────────────────────┘
        discovers│                            │ rate check
                 ▼                            ▼
        Service Discovery (:8081)       RLaaS (:8085) ── redis :6379
                 │                                  └── postgres :5432
                 ▼
    Sample-1 (:8082)   Sample-2 (:8083)   Sample-3 (:8084)
```

## Requirement

- Docker (Compose v2.20+)

## Quickstart

```sh
git clone <this-repo> && cd Microservice-Architecture
docker compose up -d --build
```

Wait 60–90s for everything to register with Eureka, then try:

```sh
# no-auth route -> 200
curl http://localhost:8080/product

# auth route, bad/missing token -> 401
curl -H "x-api-key: $(grep RLAAS_API_KEY .env | cut -d= -f2)" http://localhost:8080/profile

# health
curl http://localhost:8080/actuator/health
docker ps | grep microservice-architecture
```

Authenticated requests need a JWT signed with the `JWT_SECRET` in `.env` (HS256, 30-min TTL) sent as `Authorization: Bearer <token>`.

## Services

| Port | Service | Notes |
|---|---|---|
| 8080 | API Gateway | filters, routing, `routes.yml` config |
| 8081 | Service Discovery | Eureka registry |
| 8082 | Sample Microservice 1 | `/profile` (auth required) |
| 8083 | Sample Microservice 2 | `/product` (public) |
| 8084 | Sample Microservice 3 | `/order` (public) |
| 8085 | RLaaS | token-bucket rate limiter |
| 6379 | Redis | RLaaS buckets |
| 5432 | Postgres | RLaaS client/endpoint config |
| 4317/4318 | OpenTelemetry Collector | traces/metrics from the gateway |

### Routes (API-Gateway `routes.yml`)

| Path | Service | x-api-key | JWT |
|---|---|---|---|
| `/profile` | Sample-1 | required | required |
| `/product` | Sample-2 | — | — |
| `/order` | Sample-3 | — | — |
| `/authenticate` | Sample-1 | — | — |

## Rate limiting (optional demo)

RLaaS fails open — with no client/endpoint rows the limiter allows everything. To see it limiter in action, register the gateway client + a limited endpoint:

```sh
./scripts/bootstrap.sh
# then: 6 quick hits on /order -> 6th returns 429
curl -s -o /dev/null -w "%{http_code}\n" -H "x-api-key: $(grep RLAAS_API_KEY .env | cut -d= -f2)" http://localhost:8080/order
```

`bootstrap.sh` is idempotent: creates the `API Gateway` client (using `RLAAS_API_KEY` from `.env`) and sets `/order` → 5 req/min, `/product` → 10000 req/min.

## Structure

```
├── API-Gateway/          # Spring Boot gateway (tests, docs/load-test-results.md)
├── Service-Discovery/    # Eureka server
├── Microservices/        # Sample-Microservice-1/2/3 (Spring Boot)
├── RLaaS/                # Token-bucket rate limiter
├── otel/                 # OpenTelemetry collector config + javaagent
├── scripts/bootstrap.sh  # optional RLaaS seed
└── docker-compose.yml    # entire stack
```

## Security note

The `.env` file (JWT secret + RLaaS API key) and the Postgres password in `docker-compose.yml` are **committed intentionally**: this is a local-only portfolio demo with no production data. Treat them as public — never reuse these credentials anywhere real. To use your own, edit `.env` and restart `docker compose up -d --build`.

## Tests & load results

- API Gateway: 69 tests green; JaCoCo line 270/271, branch 57/58 — see `API-Gateway/README.md`.
- Load test: ~270 authenticated req/s sustained (p95 ≈ 94 ms, 0 errors), full conditions in `API-Gateway/docs/load-test-results.md`.