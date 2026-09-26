# PiedraAzul - Medical Appointment Scheduling System

PiedraAzul is a medical appointment management and scheduling system developed as an academic project for Software Engineering.

The application is designed for medical offices, small healthcare centers, doctors, and therapists who need to manage patients, medical staff, availability, appointments, appointment history, status changes, exports, and notifications in a secure and traceable way.

The current version uses an Angular web application as the frontend and a Spring Boot modular monolith as the backend.

---

## Project Documentation

Project documentation:

[Google Docs - PiedraAzul](https://docs.google.com/document/d/1C-UVo5VodQ6efjuM3hs427Jah9dSHliqgyZF1kPODr8/edit?usp=sharing)

[Jira - PiedraAzul](https://unicauca-team-i1l9rsfo.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog?epics=visible)

---

## Problem It Solves

In many medical offices and healthcare centers, appointment scheduling is still handled through phone calls, messages, spreadsheets, or manual records.

This can result in:

- Scheduling conflicts.
- Duplicate information.
- Loss of traceability.
- Difficulty controlling availability for each doctor or therapist.
- Risk of unauthorized access to information.
- Increased operational workload for administrative staff.
- Difficulty tracking appointment changes.
- Difficulty identifying available time slots.

PiedraAzul transforms this process into a centralized digital scheduling system with authentication, role-based authorization, configurable availability, appointment lifecycle control, traceability, and schedule management.

---

## Value Proposition

PiedraAzul provides more than basic appointment creation.

The system supports the complete medical scheduling workflow through:

- Role-based access control.
- Doctor and therapist management.
- Patient management.
- Configurable medical availability.
- Dynamic appointment time slots.
- Patient self-service scheduling.
- Scheduler-assisted appointment management.
- Appointment rescheduling.
- Appointment cancellation.
- Appointment status transitions.
- Appointment history.
- Appointment traceability.
- CSV schedule export.
- Internal notification processing.
- Optional email notifications.

The main quality attributes considered are:

### Security

- Authentication with Keycloak.
- JWT token validation.
- Role-based authorization.
- Protected backend endpoints.
- Restricted functionality according to user role.

### Usability

- Web interfaces adapted to each role.
- Clear forms and actions.
- Availability visualization.
- Appointment lifecycle management.
- Confirmation and validation messages.
- Spanish and English interface support.

### Modifiability

- Modular backend architecture.
- Separation between domain, application, and infrastructure concerns.
- Ports and adapters.
- Independent business modules inside a single application.
- Reduced coupling between components.

### Traceability

- Appointment status changes are recorded.
- Rescheduling operations preserve previous and new values.
- Observations are stored with appointment changes.
- Appointment history remains available to authorized users.

---

# Technologies Used

## Frontend

- Angular
- TypeScript
- HTML
- SCSS
- Keycloak JS

## Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Mail
- Maven

## Infrastructure

- MariaDB
- Keycloak
- JWT
- Docker
- Docker Compose

## Architecture and Design

- Modular Monolith
- Hexagonal Architecture
- Ports and Adapters
- Factory Pattern
- Observer Pattern
- Builder Pattern
- Adapter Pattern

---

# General Architecture

The current version of PiedraAzul uses a web frontend and a single Spring Boot backend.

The backend is implemented as a modular monolith.

The main project components are:

- `piedraazul-web`: Angular frontend.
- `piedraazul-agenda-service`: Spring Boot modular monolith.
- `keycloak`: Keycloak realm configuration.
- `docs`: project documentation and evidence.
- `docker-compose.yml`: local infrastructure orchestration.

The application does not use independent business microservices.

All backend business functionality is contained inside a single Spring Boot deployable application.

---

# Modular Monolith

The backend is located in:

```text
piedraazul-agenda-service
```

Its main Java package is:

```text
co.edu.unicauca.piedraazul.agenda
```

The backend is divided into functional modules.

The main modules are:

- `identity`
- `doctors`
- `patients`
- `appointments`
- `availability`
- `notifications`
- `shared`

Each module owns a specific business responsibility while remaining part of the same Spring Boot application and runtime process.

This architecture preserves separation between business areas without introducing the infrastructure and communication complexity associated with distributed microservices.

---

# Backend Modules

## Identity Module

The Identity module is responsible for authentication-related application logic and integration with Keycloak.

Its responsibilities include:

- User management.
- Authentication support.
- Role management.
- Password operations.
- Keycloak synchronization.
- User registration integration.

The application supports the following roles:

- `ADMIN`
- `SCHEDULER`
- `DOCTOR`
- `PATIENT`

---

## Doctors Module

The Doctors module is responsible for doctor and therapist management.

Supported operations include:

- Registering doctors.
- Listing doctors.
- Editing doctor information.
- Removing doctors.
- Associating doctors with authentication users.
- Managing specialties.
- Managing appointment duration.
- Managing doctor active status.

---

## Patients Module

The Patients module is responsible for patient information and patient-related operations.

The patient information is used during:

- Appointment creation.
- Appointment history visualization.
- Patient identification.
- Scheduling workflows.

---

## Availability Module

The Availability module is responsible for configuring and calculating doctor availability.

Administrators can configure:

- Day of the week.
- Start time.
- End time.
- Appointment interval.
- Scheduling window.

For example, an administrator can configure:

- Day: Tuesday.
- Start time: 13:00.
- End time: 15:00.
- Appointment interval: 30 minutes.
- Scheduling window: 4 weeks.

The system dynamically generates appointment slots from the configured schedule.

For the previous example, the generated slots are:

- 13:00
- 13:30
- 14:00
- 14:30

The end time is treated as the schedule limit and is not generated as an appointment slot.

Existing appointments are automatically removed from the available time slots.

For example, if a patient schedules an appointment at 13:30, the next availability query will only display:

- 13:00
- 14:00
- 14:30

---

## Appointments Module

The Appointments module is responsible for the complete appointment lifecycle.

Supported operations include:

- Appointment creation.
- Appointment lookup.
- Appointment confirmation.
- Appointment cancellation.
- Appointment rescheduling.
- Marking appointments as attended.
- Appointment history.
- Doctor schedule lookup.
- Patient appointment history.
- Appointment status changes.
- CSV schedule export.

The normal appointment lifecycle is:

1. `PROGRAMADA`
2. `CONFIRMADA`
3. `ATENDIDA`

An appointment can also transition to:

- `CANCELADA`

depending on the applicable business rules.

Once an appointment reaches a final state, actions that are no longer valid are disabled.

---

## Notifications Module

Notifications are implemented as an internal backend module.

There is no independent notification microservice.

The notification module contains:

- Domain models.
- Application services.
- Output ports.
- Persistence adapters.
- Email integration.

Notification processing occurs inside the same Spring Boot modular monolith.

Optional email delivery is supported through Spring Mail.

Email behavior can be configured with environment variables such as:

```text
NOTIFICATION_MAIL_ENABLED
NOTIFICATION_MAIL_FROM
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
```

---

## Shared Module

The Shared module contains functionality and abstractions that can be reused by multiple business modules.

Its purpose is to avoid unnecessary duplication while keeping business modules separated.

---

# Hexagonal Architecture

The backend applies hexagonal architecture principles to isolate business logic from infrastructure concerns.

The application is organized around the following concepts.

## Domain

Contains:

- Business models.
- Business rules.
- Core domain concepts.

## Application

Contains:

- Application services.
- Use cases.
- Business orchestration.

## Input Ports

Define operations exposed by the application.

These ports represent actions that can be performed by users or external interfaces.

## Output Ports

Define contracts required by application logic.

Examples include:

- Persistence operations.
- Keycloak operations.
- Notification persistence.
- Email sending.

## Input Adapters

Input adapters expose application functionality.

The main input adapters are REST controllers.

## Output Adapters

Output adapters connect application logic with infrastructure.

Examples include:

- MariaDB persistence.
- Keycloak communication.
- Email delivery.

This organization prevents core business logic from depending directly on controllers, databases, or external infrastructure technologies.

---

# Frontend

The frontend is located in:

```text
piedraazul-web
```

It is implemented with Angular.

The web application provides dedicated portals according to the authenticated user's role.

The application currently includes:

- Administrator Portal.
- Scheduler Portal.
- Doctor Portal.
- Patient Portal.

The interface supports:

- Spanish.
- English.

---

# Administrator Portal

Administrators can manage doctors and availability configuration.

Main functionality includes:

- List registered doctors.
- Register doctors.
- Edit doctor information.
- Delete doctors.
- View doctor status.
- Configure doctor availability.
- Create availability schedules.
- Modify availability schedules.
- Configure appointment intervals.
- Configure scheduling windows.

An administrator can define schedules for different days of the week.

Example:

```text
Doctor: Jhoiner Puentes
Day: Tuesday
Schedule: 13:00 - 15:00
Appointment interval: 30 minutes
Scheduling window: 4 weeks
```

The configuration is immediately reflected in the availability shown to patients.

---

# Scheduler Portal

Schedulers can manage the medical schedule.

Main functionality includes:

- Select a doctor or therapist.
- Select a date.
- View the doctor's schedule.
- View appointment information.
- Reschedule appointments.
- Cancel appointments.
- Export schedules to CSV.

The exported CSV contains information such as:

- Appointment ID.
- Patient.
- Doctor.
- Date.
- Time.
- Status.
- Notes.

---

# Doctor Portal

Doctors can manage the appointments assigned to them.

Main functionality includes:

- View their own profile.
- View specialty.
- View appointment duration.
- Select a date.
- View assigned appointments.
- Confirm scheduled appointments.
- Mark confirmed appointments as attended.
- Cancel eligible appointments.
- Add observations during status changes.

The normal status flow is:

```text
PROGRAMADA -> CONFIRMADA -> ATENDIDA
```

For example, a doctor can confirm an appointment and later mark it as attended.

When an appointment reaches the `ATENDIDA` state, no additional appointment actions are available.

---

# Patient Portal

Patients can manage their own appointments.

Main functionality includes:

- View profile information.
- Select a doctor or therapist.
- Select an appointment date.
- Consult available time slots.
- Schedule appointments.
- View appointment history.
- Reschedule eligible appointments.
- Cancel eligible appointments.
- View appointment status changes.

Available time slots are calculated dynamically.

When a patient creates an appointment, the selected slot becomes unavailable for future reservations.

---

# Appointment Traceability

PiedraAzul preserves information about appointment changes.

For example, when an appointment changes from scheduled to confirmed, the appointment observations can contain information such as:

```text
Cambio de estado de PROGRAMADA a CONFIRMADA.
Observación: Consulta General confirmada.
```

When the appointment is attended:

```text
Cambio de estado de CONFIRMADA a ATENDIDA.
Observación: Consulta realizada correctamente.
```

Rescheduling operations also preserve information about:

- Previous date.
- Previous time.
- New date.
- New time.
- Responsible actor.
- Reason for the change.

This provides traceability throughout the appointment lifecycle.

---

# Authentication and Authorization

Authentication is handled by Keycloak.

The Angular frontend redirects the user to Keycloak to authenticate.

After successful authentication, Keycloak provides a JWT access token.

Angular sends this token to the Spring Boot backend using the HTTP Authorization header.

Example:

```text
Authorization: Bearer <token>
```

The backend operates as an OAuth2 Resource Server and validates the JWT issued by Keycloak.

Application roles are extracted from the Keycloak token.

The roles currently supported are:

- `ADMIN`
- `SCHEDULER`
- `DOCTOR`
- `PATIENT`

---

# Backend Security

Backend endpoints are protected according to user roles.

Examples include:

- Doctor management is restricted to administrators.
- Availability configuration is restricted to administrators.
- Appointment operations require authenticated users.
- Schedule export is restricted to authorized roles.
- Doctor information can be consulted only by permitted roles.

The frontend also uses Angular route guards to prevent users from accessing portals that do not correspond to their role.

---

# Business Validations

The system implements multiple business validations.

These include:

- Doctor availability validation.
- Appointment interval validation.
- Scheduling window validation.
- Existing appointment validation.
- Appointment status transition validation.
- Active doctor validation.
- Time-slot occupation validation.
- Appointment rescheduling validation.
- Appointment history registration.
- Role-based access validation.
- Prevention of duplicate occupation of the same appointment slot.

---

# CSV Export

The Scheduler Portal allows medical schedules to be exported to CSV.

The exported file contains columns such as:

```text
ID Appointment
Patient
Doctor
Date
Time
Status
Notes
```

This functionality allows schedule information to be opened using applications such as Microsoft Excel.

---

# Internationalization

The Angular frontend supports two languages:

- Spanish.
- English.

Texts are centralized in the frontend translation configuration.

The user can change the interface language from the application navigation area.

---

# Project Structure

The current project contains the following main directories and files:

- `.github`
- `.vscode`
- `docs`
- `keycloak`
- `piedraazul-agenda-service`
- `piedraazul-web`
- `.gitignore`
- `docker-compose.yml`
- `README.md`

Legacy JavaFX components and the previous standalone notification service are no longer part of the current architecture.

---

# Docker Compose

Docker Compose is used to run the infrastructure and backend required by the application.

The current Docker Compose configuration includes:

- MariaDB.
- Keycloak.
- PiedraAzul Agenda Service.

There is no independent notification service container.

---

# Local Ports

| Component | Port |
|---|---:|
| Angular | 4200 |
| Agenda Service | 8081 |
| Keycloak | 8085 |
| MariaDB | 3307 |

---

# Keycloak Configuration

The Keycloak realm configuration is stored in:

```text
keycloak/piedrazul-realm.json
```

Docker Compose imports this realm when Keycloak starts.

The configured realm is:

```text
PiedrAzul
```

The Angular client is:

```text
piedraazul-spa
```

The backend also communicates with Keycloak for authentication and user management operations.

---

# Running the Project

The project requires:

- Java 17.
- Maven.
- Node.js.
- npm.
- Docker.
- Docker Compose.

---

## Start Infrastructure and Backend

From the root directory of the project, execute:

```bash
docker compose up --build -d
```

This starts:

- MariaDB.
- Keycloak.
- PiedraAzul Agenda Service.

To verify running containers:

```bash
docker compose ps
```

To view backend logs:

```bash
docker compose logs -f agenda-service
```

---

## Run the Angular Frontend

Open another terminal and navigate to:

```bash
cd piedraazul-web
```

Install dependencies if required:

```bash
npm install
```

Start the Angular development server:

```bash
npm start
```

The frontend is available at:

```text
http://localhost:4200
```

---

# Backend Development

Navigate to:

```bash
cd piedraazul-agenda-service
```

Make sure Java 17 is active.

You can verify it with:

```bash
java -version
```

You can also verify the Java version used by Maven:

```bash
mvn -version
```

---

# Backend Tests

Run the complete backend test suite with:

```bash
mvn test
```

Current validated test result:

```text
Tests run: 85
Failures: 0
Errors: 0
Skipped: 0
```

The complete backend test suite currently passes successfully using Java 17.

---

# Frontend TypeScript Validation

Navigate to:

```bash
cd piedraazul-web
```

Run:

```bash
npx tsc -p tsconfig.app.json --noEmit
```

A successful validation returns to the terminal without TypeScript compilation errors.

---

# Useful Docker Commands

## Start the Application Infrastructure

```bash
docker compose up --build -d
```

## View Running Containers

```bash
docker compose ps
```

## View Backend Logs

```bash
docker compose logs -f agenda-service
```

## Stop Containers

```bash
docker compose down
```

## Stop Containers and Remove Volumes

```bash
docker compose down -v
```

The last command should be used carefully because it removes persistent local MariaDB and Keycloak volumes.

---

# Test Users

The development environment contains users for the main application roles.

| Role | Username |
|---|---|
| Administrator | `admin` |
| Scheduler | `scheduler` |
| Doctor | `doctor` |
| Patient | `patient` |

Passwords used for local testing should not be reused in production environments.

---

# Architectural Decision

PiedraAzul uses a modular monolith instead of independent business microservices.

This decision means:

- There is a single Spring Boot backend application.
- Business capabilities remain separated into modules.
- Modules execute inside the same application process.
- Internal business modules do not communicate through HTTP.
- RabbitMQ is not required for communication between business modules.
- Kafka is not required for communication between business modules.
- Notifications are processed internally.
- A single backend deployment contains the complete application business logic.

This architecture maintains modularity while reducing the deployment, networking, synchronization, and operational complexity associated with distributed microservices.

---

# Migration from the Previous Architecture

Earlier versions of the project included:

- A JavaFX desktop client.
- A separate notification service.
- A microservice-oriented structure.

The current version replaced those components with:

- Angular web frontend.
- Spring Boot modular monolith.
- Internal notifications module.

The JavaFX client and standalone notification service were removed from the current project.

---

# Current Project Status

The current application includes:

- Angular web frontend.
- Spring Boot modular monolith.
- MariaDB persistence.
- Keycloak authentication.
- JWT authorization.
- Role-based access control.
- Doctor management.
- Patient management.
- Availability management.
- Dynamic time-slot calculation.
- Appointment scheduling.
- Appointment cancellation.
- Appointment rescheduling.
- Appointment confirmation.
- Appointment attendance management.
- Appointment history.
- Appointment traceability.
- CSV schedule export.
- Internal notification processing.
- Optional email notification support.
- Spanish and English frontend support.
- Automated backend tests.

---

# Product Value

PiedraAzul transforms a manual or disorganized appointment scheduling process into a controlled, secure, and traceable digital system.

Its main value includes:

- Reducing scheduling errors.
- Preventing appointment conflicts.
- Saving administrative time.
- Improving the patient experience.
- Controlling doctor and therapist availability.
- Protecting access to information.
- Preserving appointment traceability.
- Simplifying schedule management.
- Providing clear separation of responsibilities through modular architecture.
- Facilitating future system evolution.

---

# Authors

Academic project developed for Software Engineering.

Development team:

- Charry Vela Brayan
- Puentes Figueroa Jhoiner
- Majé Bonilla Santiago
- Narvaez Canchala Julian
