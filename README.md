# 🏠 Household Accounts Management System

> Sistema de Gestión de Cuentas del Hogar — a family/personal expense tracking REST API built with **Java 17** and **Spring Boot 3**, following **Domain-Driven Design (DDD)**, **Hexagonal Architecture**, and **Spec-Driven Development (SDD)**.

| Español | English |
|---|---|
| API REST para gestionar los gastos del hogar: registro de compras con pagos en cuotas, categorización de gastos y reportes analíticos con proyecciones futuras. | REST API to manage household expenses: purchase registration with installment payments, expense categorization, and analytical reports with future projections. |

---
1 | 
2

## Features / Características

**English**

- 👥 **Users** — manage household members enabled to record expenses (each purchase is attributable to a user).
- 💳 **Purchases & Installments** — register cash or installment purchases; the domain auto-generates `N` installments for the following months.
- 🗂️ **Categorization** — classify by expense type (fixed / variable), scope (home / outing / personal) and category (supermarket, basic services, health, education, entertainment…).
- 📊 **Reports & Projections** — monthly dashboard, annual evolution by category/scope, and future financial commitments based on pending installments.
- 🧪 **Quality** — 43 automated tests with **JaCoCo** coverage gate ≥ 80% line coverage.

**Español**

- 👥 **Usuarios** — gestión de los miembros del hogar habilitados para registrar gastos (cada compra es atribuible a un usuario).
- 💳 **Compras y cuotas** — registro de compras al contado o en cuotas; el dominio genera automáticamente `N` cuotas para los meses siguientes.
- 🗂️ **Categorización** — clasificación por tipo de gasto (fijo / variable), ámbito (hogar / salidas / personal) y categoría (supermercado, servicios básicos, salud, educación, entretenimiento…).
- 📊 **Reportes y proyecciones** — panel mensual, evolución anual por categoría/ámbito y compromisos financieros futuros según las cuotas pendientes.
- 🧪 **Calidad** — 43 pruebas automatizadas con umbral de cobertura **JaCoCo** ≥ 80% de líneas.

---

## Tech Stack / Stack Tecnológico

| Layer / Capa | Technology / Tecnología |
|---|---|
| Language / Lenguaje | Java 17 (LTS) |
| Framework | Spring Boot 3.3.5 |
| Persistence / Persistencia | Spring Data JPA, Hibernate, PostgreSQL 15 (Docker) |
| API Docs | OpenAPI 3 + Swagger UI (springdoc-openapi) |
| Build / Compilación | Maven 3.9.x |
| Testing / Pruebas | JUnit 5, Mockito, H2, MockMvc |
| Coverage / Cobertura | JaCoCo (≥ 80% línea) |
| Boilerplate | Lombok |

---

## Architecture / Arquitectura

The codebase follows **Hexagonal (Ports & Adapters)** architecture combined with **DDD**:

```text
src/
├── main/java/drl/desafio/
│   ├── domain/            # Pure business logic (framework-independent)
│   │   ├── entity/        # User, Purchase, Installment, enums...
│   │   ├── repository/    # Repository interfaces (ports)
│   │   ├── exception/     # Domain exceptions (e.g. InvalidPurchaseException)
│   │   └── vo/            # Value objects (Money, UserId, ...)
│   ├── application/       # Use cases (register purchase, dashboard, ...)
│   │   ├── port/          # Input ports / DTOs
│   │   └── service/       # RegisterPurchaseUseCase, DashboardService, ...
│   └── infrastructure/    # Technical details and adapters
│       ├── persistence/   # JPA entities, Spring Data repositories, mappers
│       ├── rest/          # REST controllers, OpenAPI/Swagger config
│       └── config/        # .env loading, bean configuration
└── test/java/drl/desafio/
    ├── domain/            # Unit tests of business rules (no Spring)
    ├── application/       # Unit tests of use cases (Mockito)
    └── infrastructure/    # Integration tests (H2 / MockMvc)
```

---

## Getting Started / Puesta en Marcha

### Prerequisites / Requisitos

- **JDK 17** (LTS)
- **Maven 3.9+**
- **Docker** (for PostgreSQL)

### 1. Clone the repository / Clonar el repositorio

```bash
git clone <repo-url>
cd Desafio4
```

### 2. Configure the database / Configurar la base de datos

Create a `.env` file in the project root (see [Environment Variables](#environment-variables--variables-de-entorno)) and start PostgreSQL:

```bash
docker compose up -d
```

### 3. Run / Ejecutar

```bash
mvn spring-boot:run
```

The application starts at → **http://localhost:8080** ✅

Swagger UI → **http://localhost:8080/swagger-ui.html**

---

## Environment Variables / Variables de Entorno

Defined in a `.env` file at the project root and loaded via `spring-dotenv`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hogar_db
DB_USER=postgres
DB_PASSWORD=postgres_secret
```

---

## API Usage / Uso de la API

Base URL: **`/api/v1`**

### 👥 Users / Usuarios

**`POST /api/v1/users`** — Create user / Crear usuario

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan Perez","email":"juan.perez@example.com"}'
```

```json
// 201 Created
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "name": "Juan Perez",
  "email": "juan.perez@example.com",
  "active": true
}
```

### 💳 Purchases / Compras

**`POST /api/v1/purchases`** — Register purchase (in 3 installments) / Registrar compra (en 3 cuotas)

```bash
curl -X POST http://localhost:8080/api/v1/purchases \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
    "totalAmount": 150000.00,
    "purchaseDate": "2026-03-01",
    "paymentMethod": "CREDIT_CARD",
    "financialInstitution": "BANCO_DE_CHILE",
    "installmentCount": 3,
    "expenseType": "VARIABLE",
    "scope": "HOME",
    "category": "ELECTRODOMESTICOS"
  }'
```

```json
// 201 Created
{
  "id": "c71a3674-8b09-42ef-91f8-011111111111",
  "userId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "totalAmount": 150000.00,
  "installmentCount": 3,
  "installments": [
    { "number": 1, "amount": 50000.00, "period": "2026-03" },
    { "number": 2, "amount": 50000.00, "period": "2026-04" },
    { "number": 3, "amount": 50000.00, "period": "2026-05" }
  ]
}
```

### 📊 Reports / Reportes

**`GET /api/v1/reports/dashboard?month=3&year=2026`** — Monthly dashboard / Panel mensual

```bash
curl "http://localhost:8080/api/v1/reports/dashboard?month=3&year=2026"
```

**`GET /api/v1/reports/projection?months=6`** — Future projection / Proyección futura

```bash
curl "http://localhost:8080/api/v1/reports/projection?months=6"
```

> A complete Bruno/Postman collection is available at **`bruno-collection.json`** — import it directly into Bruno or Postman.

---

## Testing / Pruebas

Run the full test suite with the JaCoCo coverage gate:

```bash
mvn verify
```

```text
Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.   # JaCoCo ≥ 80% line coverage
```

| Test layer / Capa | What it verifies / Qué verifica |
|---|---|
| `domain/entity` | Business rules & proration algorithm, without Spring |
| `application/service` | Use cases with Mockito |
| `infrastructure` | REST controllers & end-to-end integration (H2 / MockMvc) |

---

## Documentation / Documentación

The detailed specification documents (in **English**) live in the **[`resources/`](./resources)** folder:

| File / Archivo | Content / Contenido |
|---|---|
| `system-overview.md` | System overview, DDD modules & architecture |
| `api-spec.md` | REST contract & DTOs |
| `domain-model-spec.md` | Domain model |
| `requirements-spec.md` | Functional requirements |
| `technical-setup-spec.md` | Docker, environment & testing setup |

---

## License / Licencia

This is a personal challenge project (`drl.desafio`). No license is specified — contact the author for usage rights.

> Proyecto de desafío personal (`drl.desafio`). No se especifica licencia — contacta al autor para derechos de uso.
