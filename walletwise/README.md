# WalletWise

A simple wallet service built with Spring Boot 3, PostgreSQL, Redis, RabbitMQ and JWT authentication.

## Prerequisites

1. Java 17+
2. Maven 3.8+
3. PostgreSQL running on `localhost:5432` with database `walletwise` and user `postgres/password` (adjust in `application.yml`).
4. Redis running on `localhost:6379`.
5. RabbitMQ running on `localhost:5672` (default guest/guest).

## Run Locally

```bash
cd walletwise
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

## API Overview

| Endpoint | Method | Description |
|----------|--------|-------------|
| /api/auth/register | POST | Register new user (creates wallet) |
| /api/auth/login | POST | Login and receive JWT |
| /api/wallet/balance | GET | Get balance (JWT required) |
| /api/wallet/add | POST | Add money (JWT required) |
| /api/wallet/transfer | POST | Transfer between users (JWT required) |
| /api/wallet/history | GET | Transaction history (JWT required) |
| /api/otp/send | POST | Generate & send OTP |
| /api/otp/verify | POST | Verify OTP |

Refer to `postman_collection.json` for sample requests.

## Building the JAR

```bash
mvn clean package -DskipTests
```

## Docker Compose (optional)

If you prefer, create a `docker-compose.yml` with PostgreSQL, Redis, and RabbitMQ and update the connection settings.