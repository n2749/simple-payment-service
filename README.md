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

## API docs

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON (live): `http://localhost:8080/v3/api-docs`
- OpenAPI JSON (saved): `docs/openapi/openapi.json`
- Postman collection: `docs/postman/payment-service.postman_collection.json`

## Tests

```bash
docker compose up -d postgres
docker run --rm \
  -v "$PWD":/workspace \
  -w /workspace \
  -v "$HOME/.m2":/root/.m2 \
  --add-host=host.docker.internal:host-gateway \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/payment_db \
  -e SPRING_DATASOURCE_USERNAME=payment_user \
  -e SPRING_DATASOURCE_PASSWORD=payment_pass \
  maven:3.9.9-eclipse-temurin-21 \
  mvn test
```

or if you have maven installed:

```bash
docker compose up -d postgres
mvn test
```
