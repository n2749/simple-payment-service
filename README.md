# Payment Service API

[![Tests](https://github.com/n2749/simple-payment-service/actions/workflows/ci.yml/badge.svg)](https://github.com/n2749/simple-payment-service/actions)

## What it does
A production-ready REST API for processing and tracking payments, built with Spring Boot and PostgreSQL. Demonstrates modern backend practices: JWT auth, OpenAPI documentation, Docker containerization, and automated testing.

## Why you'd use this
- Need a payment API template for interviews or client work
- Want to see Spring Boot + security + database + testing in one place
- Learning OpenAPI and Docker best practices

## Stack
- Java 21, Spring Boot 3.x
- PostgreSQL
- JWT authentication (Spring Security)
- OpenAPI 3.0 (Springdoc)
- Docker & Docker Compose
- JUnit 5, Testcontainers

## Key features
1. **Create & track payments** — RESTful endpoints with validation
2. **JWT authentication** — role-based access control
3. **OpenAPI documentation** — interactive Swagger UI built-in
4. **Full test coverage** — unit + integration tests with Testcontainers
5. **One-command deploy** — `docker compose up` and you're running

## Quick start
```bash
docker compose up --build
# API: http://localhost:8080
# Docs: http://localhost:8080/swagger-ui.html
```

## Tests
```bash
mvn test
```

## What I'd change if building for production
- Async payment processing (message queue)
- Rate limiting on endpoints
- Audit logging for compliance
- Idempotency keys for retries

## License
MIT
