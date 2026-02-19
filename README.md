# Spring Backend Starter

A production-oriented **Spring Boot 3** backend template with JWT authentication, role-based access control (RBAC), Redis-backed refresh tokens, and MySQL + Flyway. Use it as a clone-and-go base for APIs.

---

## What's included

| Area | Features |
|------|----------|
| **Auth** | JWT access + refresh tokens, Redis or DB session store, login / logout / refresh, `GET /api/v1/auth/me/authorities` |
| **Users** | CRUD with `UserDto`, `@PreAuthorize` by authority (e.g. `USER_READ`, `USER_CREATE`), DTO projections |
| **Roles & Authorities** | Roles (e.g. ADMIN, USER), granular authorities (USER_CREATE, ROLE_ASSIGN, etc.), assign/remove authorities to roles |
| **Data** | MySQL 8, JPA/Hibernate, Flyway migrations, optional Spring Data REST |
| **Infra** | Spring Security (stateless), BCrypt passwords, Spring Cache, Kafka on classpath (ready for event-driven modules), global REST exception handling with structured error responses (including `traceId`) |

---

## Tech stack

- **Java 17** · **Spring Boot 3.5** · **Maven**
- **Spring Security** + **JWT (JJWT)** + **Redis** (or DB) for refresh token storage
- **MySQL 8** + **Flyway** + **Spring Data JPA**
- **Spring Kafka** (optional)
- **Lombok** · **Bean Validation**

---

## Quick start

### 1. Prerequisites

- **Java 17**
- **Maven 3.8+**
- **MySQL 8+** (create a database, e.g. `spring_backend_starter`)
- **Redis 7+** (for refresh token storage; can switch to DB via config)

### 2. Configure

Copy or edit `src/main/resources/application.properties`:

- **Database:** `spring.datasource.url`, `username`, `password`
- **Redis:** `spring.redis.host`, `spring.redis.port`
- **JWT:** `application.security.jwt.secret.key` (use a strong secret in production)
- **Session store:** `application.security.session.store=redis` or `db`

### 3. Run

```bash
./mvnw spring-boot:run
```

Flyway will run migrations; an **admin** user is created if missing (see [Default credentials](#default-credentials)).

### 4. Try the API

**Login** (returns `accessToken` and `refreshToken`):

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Call a protected endpoint** (replace `YOUR_ACCESS_TOKEN`):

```bash
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  http://localhost:8080/api/v1/users
```

---

## API overview

| Method | Path | Description | Auth |
|--------|------|-------------|------|
| `POST` | `/api/v1/auth/login` | Login; returns access + refresh tokens | Public |
| `POST` | `/api/v1/auth/logout` | Invalidate refresh token | Public (body: refreshToken) |
| `POST` | `/api/v1/auth/refresh-token` | Issue new access + refresh tokens | Public (body: refreshToken) |
| `GET`  | `/api/v1/auth/me/authorities` | Current user's authorities | JWT |
| `GET/POST/PUT/DELETE` | `/api/v1/users` | User CRUD | JWT + authority |
| `GET/POST/PUT/DELETE` | `/api/v1/roles` | Role CRUD; assign/remove authorities | JWT + authority |
| `GET`  | `/api/v1/authorities` | List authorities | JWT + ROLE_ASSIGN |

Protected routes require the `Authorization: Bearer <accessToken>` header. Authorities (e.g. `USER_READ`, `ROLE_CREATE`) are enforced with `@PreAuthorize`.

---

## Error handling

All errors are normalized into a consistent JSON shape via `GlobalExceptionHandler` and the `ApiError` record:

- **Authentication & authorization failures** use `RestAuthenticationEntryPoint` / JWT filter responses.
- **Validation errors**, **business exceptions** (subclasses of `AppException`), and unexpected errors are converted into the same envelope.

Example error payload:

```json
{
  "timestamp": "2026-02-07T12:34:56.789Z",
  "status": 400,
  "error": "Validation Error",
  "message": "username: must not be blank",
  "path": "/api/v1/users",
  "traceId": "abc123..."
}
```

---

## Default credentials

After the first run (with Flyway), a default admin user exists:

- **Username:** `admin`
- **Password:** `admin123`

Change the password in production; the hash is in the Flyway migration and in comments in `application.properties`.

---

## Configuration reference

| Property | Description | Example |
|----------|-------------|---------|
| `spring.datasource.url` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/spring_backend_starter` |
| `spring.redis.host` / `spring.redis.port` | Redis connection | `localhost` / `6379` |
| `application.security.jwt.secret.key` | Secret for signing JWTs | Long random string (e.g. 64 hex chars) |
| `application.security.jwt.access.expiration` | Access token TTL (ms) | `86400000` (24h) |
| `application.security.jwt.refresh.expiration` | Refresh token TTL (ms) | `604800000` (7d) |
| `application.security.session.store` | Where to store refresh tokens | `redis` or `db` |

---

## Project structure (high level)

```
src/main/java/.../spring_backend_starter/
├── config/          # Security, Redis, password encoder
├── controller/      # Auth, User, Role, Authority REST APIs
├── dto/             # Request/response DTOs
├── entity/          # JPA entities (User, Role, Authority, AuthSession, etc.)
├── filter/          # JWT auth filter
├── projection/      # DTO projections for entities
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic; AuthSessionStore (Redis/DB impl)
└── exception/       # Custom exceptions, global handler (`GlobalExceptionHandler`), `ApiError`, auth entry point
```

---

## Using this as a starter

1. Clone the repo and rename the package/artifact if you want your own namespace.
2. Adjust Flyway migrations and entities for your domain.
3. Add or remove controllers and authorities as needed.
4. Set `application.security.jwt.secret.key` (and DB/Redis) per environment; consider `application-dev.properties` / `application-prod.properties` and never commit production secrets.

---


