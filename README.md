# simple-payment-service

Spring Boot + PostgreSQL setup for a simple payment service.

## Run with Docker Compose

Requirements: Docker + Docker Compose.

```bash
docker compose up --build
```

Services:
- `payment-service`: `http://localhost:8080`
- `postgres`: `localhost:5432` (`payment_db`)

Health check:

```bash
curl http://localhost:8080/actuator/health
```

Stop:

```bash
docker compose down
```

Stop and remove DB volume:

```bash
docker compose down -v
```
