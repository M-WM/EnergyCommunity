# Energy Community System

## 1. Design Ideas and Architecture

### 1.1 Overview

Our **Energy Community System** is composed of multiple independent microservices communicating via RabbitMQ and a REST API, complemented by a JavaFX GUI:

- **Energy Producer Service**: Generates random community production messages.
- **Energy User Service**: Sends consumption messages reflecting time-of-day demand patterns.
- **Usage Service**: Aggregates minute-level messages into hourly usage records in a PostgreSQL database, then emits update events.
- **Current Percentage Service**: Consumes usage updates, computes depletion and grid-portion percentages per hour, writes to a separate table.
- **REST API**: Spring Boot application exposing two endpoints:
  - `GET /energy/current`: returns current hour percentages
  - `GET /energy/historical`: returns historical usage
- **JavaFX GUI**: Fetches data from the REST API; displays live community pool, grid portion, and historical totals via intuitive date-time pickers.

Architecture follows a **loose coupling** paradigm: each component is independently deployable, uses RabbitMQ for asynchronous communication, and relies on the database only for state persistence and read access. Docker Compose orchestrates container networking.

### 1.2 Component Interaction Flow

1. **Producer & User services** publish messages to `energy.exchange` with routing keys `producer` or `usage`.
2. **Usage Service** listens on `usage.queue`, updates `usage_table`, then publishes a `UsageUpdateEvent` to `percentage.queue`.
3. **Percentage Service** calculates percentages, saves to `percentage_table`.
4. **GUI** requests data via REST API, which reads directly from the database.

This ensures clear separation of concerns and fault isolation.

## 2. Lessons Learned

- **Microservice Boundaries**: Defining clear service responsibilities improved maintainability; however, sharing DTOs initially created tight coupling, leading to a migration toward API-First contracts.
- **Asynchronous Patterns**: RabbitMQ facilitated decoupling but introduced complexity in ensuring idempotent listener behavior and message ordering.
- **Spring Boot & JPA**: Auto-wiring and Spring Data JPA accelerated development, but silent errors (e.g., schema mismatches) necessitated careful debug logging and Flyway migrations for reliable schema management.
- **JavaFX Integration**: Building an interactive GUI highlighted threading considerations; ensuring UI updates ran on the JavaFX Application Thread (via `Platform.runLater`) was critical.
- **Dependency Management**: Balancing shared modules versus independent versioning taught the importance of API contracts over common libraries to avoid tight coupling.

## 3. Time Tracking

| Task                                    | Estimated h | Actual h |
|-----------------------------------------|------------:|---------:|
| Architecture & Design                   |         10  |      12  |
| Energy Producer & User Services         |         12  |      14  |
| Usage Service Implementation            |         15  |      18  |
| Percentage Service Implementation       |         10  |      12  |
| REST API & Flyway Integration           |          8  |      10  |
| JavaFX GUI Development                  |         12  |      15  |
| Docker Compose & Infrastructure         |          6  |       7  |
| Testing, Debugging & Refinements        |         10  |      12  |
| Documentation & Refactoring             |          7  |       8  |
| **Total**                               |         90  |     108  |

## 4. Git History

The full development history, including commits and change logs, can be reviewed directly in the project's Git repository. It demonstrates iterative improvements, bug fixes and refactoring towards the final architecture.
[Link zum Repository](https://github.com/M-WM/EnergyCommunity)

---

_End of Documentation_
