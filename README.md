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

Backend Modules

The backend is organized by business domain instead of independent deployable services.

piedraazul-agenda-service/
└── src/main/java/co/edu/unicauca/piedraazul/agenda/
    ├── appointments/
    ├── availability/
    ├── doctors/
    ├── patients/
    ├── identity/
    ├── notifications/
    ├── configuration/
    └── shared/
Appointments

Responsible for:

Appointment creation.
Appointment queries.
Appointment status changes.
Rescheduling.
Rescheduling history.
Appointment-related business validations.
Availability

Responsible for:

Doctor and therapist working days.
Working time ranges.
Appointment intervals.
Scheduling windows.
Available time slot calculation.
Doctors

Responsible for:

Doctor and therapist management.
Doctor information.
Medical specialty information.
Patients

Responsible for:

Patient information.
Patient registration data.
Patient lookup.
Identity

Responsible for:

Users.
Roles.
Authentication integration.
Keycloak synchronization.
Notifications

Responsible for:

Appointment notification processing.
Email delivery abstraction.
Notification history persistence.

Notifications are no longer implemented as an independent microservice.

Communication between the appointments and notifications modules is performed through an internal Spring event:

CreateAppointmentService
        |
        v
AppointmentCreatedEvent
        |
        v
Spring ApplicationEventPublisher
        |
        v
AppointmentCreatedEventListener
        |
        v
NotificationService
       / \
      /   \
     v     v
Log Port   Email Port

No HTTP communication is required between these modules.

Hexagonal Architecture

Business modules maintain Hexagonal Architecture internally.

A typical module follows this structure:

module/
├── api/
│   └── event/
│
└── internal/
    ├── domain/
    │   └── model/
    │
    ├── application/
    │   ├── dto/
    │   ├── port/
    │   │   ├── in/
    │   │   └── out/
    │   └── service/
    │
    └── adapter/
        ├── in/
        │   ├── web/
        │   └── event/
        │
        └── out/
            ├── persistence/
            └── mail/

The main architectural objective is to keep business logic independent from infrastructure technologies.

Input ports define operations provided by the application.

Output ports define infrastructure capabilities required by the application.

Adapters implement those contracts using technologies such as:

Spring MVC.
Spring Data JPA.
MariaDB.
Keycloak.
Spring Mail.
Inter-Module Communication

Modules must not depend directly on the internal implementation of another module.

Communication is performed using:

Public module contracts.
Interfaces.
Internal application events.

For example, the appointments module publishes:

AppointmentCreatedEvent

and the notifications module consumes that event without knowing the internal implementation of the appointment use case.

This reduces coupling and prevents the modular monolith from becoming a traditional tightly coupled monolith.

Technologies
Java 17
Spring Boot
Spring MVC
Spring Data JPA
MariaDB
Keycloak
JWT
Maven
Docker
Docker Compose
Spring Mail
JUnit 5
Mockito
Hexagonal Architecture
Modular Monolith Architecture

The legacy frontend currently uses JavaFX and will be replaced by Angular.

Authentication and Authorization

Authentication is managed by Keycloak.

Current roles:

ADMIN
SCHEDULER
DOCTOR
PATIENT

The backend works as an OAuth2 Resource Server and validates JWT access tokens issued by Keycloak.

The authentication configuration will be migrated to the SPA authentication flow using:

Authorization Code + PKCE

when the Angular frontend is introduced.

Persistence

The modular monolith uses a single MariaDB database:

piedraazul_agenda

Current main tables include:

appointments
availabilities_doctor
doctors
history_reschedulings
notifications_log
patients
users

Each module owns the application logic associated with its data even though the modules share the same physical database.

Notification Flow

When an appointment is successfully created:

CreateAppointmentService completes the appointment creation.
The appointments module publishes an AppointmentCreatedEvent.
Spring dispatches the event inside the same application process.
AppointmentCreatedEventListener receives the event.
NotificationService processes the notification.
The notification is persisted in notifications_log.
If email delivery is enabled, the email adapter sends the message.

When email delivery is disabled for local development, notifications are recorded with:

SIMULADO

instead of sending a real email.

Design Patterns
Factory

UserFactory

Centralizes user creation according to user role.

Observer

The project uses event-based communication to decouple appointment creation from notification processing.

Builder

AppointmentResponseBuilder

Provides controlled construction of appointment response objects.

Adapter

Infrastructure implementations use adapters to connect application ports with:

MariaDB.
Keycloak.
Email.
REST infrastructure.
Strategy

Availability calculation uses interchangeable strategy logic for appointment time slots.

Business Rules

The scheduling domain includes validations such as:

Available appointment slots.
Appointment intervals.
Scheduling window.
Holidays.
Active appointment restrictions.
Doctor availability.
Rescheduling rules.
Appointment status transitions.
General consultation requirements before selected specialized services.

Existing business rules are preserved during the modular-monolith migration.

Running the Project
Requirements
Java 17
Maven
Docker
Docker Compose
Start Infrastructure

From the project root:

docker compose up -d --build

The current Docker Compose environment starts:

piedraazul-agenda-service
piedraazul-mariadb-agenda
piedraazul-keycloak

There is no independent notification service because notifications are now part of the modular monolith.

View Containers
docker compose ps
View Backend Logs
docker compose logs -f agenda-service
Stop Services
docker compose down

Do not use:

docker compose down -v

unless intentionally deleting persisted MariaDB and Keycloak volumes.

Backend Tests

Navigate to:

cd piedraazul-agenda-service

Run:

mvn clean test

The test suite includes coverage for domain models, availability strategies, observers, users, doctors, patients, appointments, and notification processing.

Default Test Users
Role	Username	Password
Administrator	admin	admin123
Scheduler	scheduler	scheduler123
Doctor	doctor	doctor123
Patient	patient	patient123
Current Migration Status
Completed
Modular backend structure.
Appointment module migration.
Doctor module migration.
Patient module migration.
Availability module migration.
Identity module migration.
Notification module migration.
Removal of HTTP communication between appointments and notifications.
Internal event-based notification communication.
Single MariaDB persistence.
Single backend deployment.
Docker Compose migration.
Existing backend tests preserved.
Notification module tests added.
Pending
Keycloak configuration for Angular SPA.
Authorization Code + PKCE.
Angular frontend.
Migration of JavaFX workflows to Angular.
Final architecture documentation.
C4 diagrams.
Next Architecture Target

The final project architecture will be:

                Angular SPA
                    |
                    | REST + JWT
                    v
        +---------------------------+
        | PiedraAzul Modular        |
        | Monolith                  |
        |                           |
        | appointments              |
        | availability              |
        | doctors                   |
        | patients                  |
        | identity                  |
        | notifications             |
        +-------------+-------------+
                      |
                      v
                   MariaDB

             Keycloak
                |
                +---- OAuth2 / OIDC ---- Angular SPA
Authors

Academic project developed for Software Engineering III.

Development team:

Charry Vela Brayan
Puentes Figueroa Jhoiner
Ruiz Segura Sebastian
