# Piedrazul-v2
PiedraAzul medical appointment scheduling system — modular monolith (Spring Boot) + Angular SPA. Architectural evolution of the original microservices project

# PiedraAzul - Medical Appointment Scheduling System

PiedraAzul is an academic medical appointment scheduling and management system developed for Software Engineering III at Universidad del Cauca.

The system manages patients, doctors and therapists, availability, appointments, rescheduling, appointment history, notifications, authentication, and role-based access control.

The backend is currently being migrated from a microservices architecture to a **Modular Monolith** based on Spring Boot and Hexagonal Architecture.

---

## Current Architecture

PiedraAzul currently consists of:

- A Spring Boot modular monolith backend.
- MariaDB for persistence.
- Keycloak for authentication and authorization.
- Docker Compose for local infrastructure.
- A legacy JavaFX desktop client that will be replaced by an Angular SPA.

The application is deployed as a single backend unit while its internal business logic is separated into independent domain modules.

```text
PiedraAzul/
├── PiedraAzul/                     # Legacy JavaFX client
├── piedraazul-agenda-service/      # Modular Monolith Backend
├── keycloak/                       # Keycloak realm configuration
├── docs/                           # Architecture and project documentation
└── docker-compose.yml              # Local infrastructure
