# mantenix-vehicle-service

Mantenix microservice that registers and manages **cars and motorcycles**: plate, brand,
model, year, type and mileage.

| Item | Value |
|------|-------|
| Port | `8083` |
| Database | PostgreSQL `mantenix_vehicles` (own database — ADR-004) |
| Stack | Java 21 · Spring Boot 4 · Maven · Flyway · springdoc-openapi (ADR-003) |
| Owner | Johann Tafur Farfán |
| Design docs | [code-sena/mantenix-docs](https://github.com/code-sena/mantenix-docs) |

---

## Run locally

Requirements: JDK 21 or newer and Docker.

```bash
# 1. Start the database (PostgreSQL 17 on localhost:5433)
docker compose up -d

# 2. Start the service (Flyway creates the tables on startup)
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

| What | URL |
|------|-----|
| Swagger UI | http://localhost:8083/swagger-ui.html |
| OpenAPI JSON | http://localhost:8083/v3/api-docs |
| Health check | http://localhost:8083/actuator/health |

Run the unit tests (no database needed): `./mvnw test`

---

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/vehicles` | Register a vehicle |
| `GET` | `/vehicles?ownerId=` | List vehicles (optional filter by owner) |
| `GET` | `/vehicles/{id}` | Get a vehicle |
| `PUT` | `/vehicles/{id}` | Update a vehicle |
| `DELETE` | `/vehicles/{id}` | Delete a vehicle |

Example request:

```json
{
  "ownerId": "550e8400-e29b-41d4-a716-446655440000",
  "plate": "ABC123",
  "type": "CAR",
  "brand": "Mazda",
  "model": "CX-30",
  "modelYear": 2022,
  "mileage": 35000
}
```

### Business rules

| Rule | Error |
|------|-------|
| The plate is unique and stored in uppercase | `409 PLATE_ALREADY_EXISTS` |
| The mileage never goes down | `409 MILEAGE_CANNOT_DECREASE` |
| Plate format `ABC123` (car) or `ABC12D` (motorcycle) | `400 VALIDATION_ERROR` |

Errors follow the shared format `{ "error", "message", "details" }`. A body that is not valid JSON or has
a wrong value (e.g. an unknown `type`) returns `400 MALFORMED_REQUEST`.

---

## Structure (layers — ADR-002)

```
src/main/java/com/mantenix/vehicle/
├── controller/   VehicleController      → REST endpoints, DTOs only
├── dto/          VehicleRequest, VehicleResponse, ErrorResponse
├── service/      VehicleService         → business rules
├── repository/   VehicleRepository      → Spring Data JPA
├── model/        Vehicle, VehicleType   → JPA entity
└── exception/    GlobalExceptionHandler and custom exceptions

src/main/resources/
├── application.properties               → ddl-auto=validate
└── db/migration/V1__create_vehicles_table.sql
```

`spring.jpa.hibernate.ddl-auto=validate`: Hibernate only checks the schema. Tables are created
and changed **only** by Flyway migrations. An applied migration is never edited — add `V2__...`.
