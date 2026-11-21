# StayScout

Hotel search and booking API built with Java 21, Spring Boot, PostgreSQL, Redis, Flyway, and JWT authentication.

## Run

```bash
export JWT_SECRET='use-a-secure-secret-with-at-least-32-characters'
export DB_PASSWORD='use-a-local-development-password'
docker compose up --build
```

The API runs on `http://localhost:8080`. Public endpoints include `GET /hotels` and `GET /hotels/{id}`. Register with `POST /auth/register`, then log in through `POST /auth/login` and send its token as `Authorization: Bearer <token>`.

## Verify

```bash
mvn verify
```

PostgreSQL enforces no-overlap bookings with a `gist` exclusion constraint; this prevents race-condition double bookings.
