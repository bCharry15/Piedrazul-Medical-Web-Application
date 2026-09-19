# PiedraAzul - Medical Appointment Scheduling System

PiedraAzul is a medical appointment management and scheduling system developed as an academic project for Software Engineering.

The application is designed for medical offices, small healthcare centers, and therapists that need to manage doctors, patients, availability, appointments, appointment history, status changes, exports, and notifications in a secure and traceable way.

The current version uses an Angular web application as the frontend and a Spring Boot modular monolith as the backend.

---

## Project Documentation

* Document: [Google Docs - PiedraAzul](https://docs.google.com/document/d/1sPHt0zLF7bGqXfEkfuj3O59Gnp1NG1h-SVsOveMMYvk/edit?usp=sharing)

---

## Problem It Solves

In many medical offices and healthcare centers, appointment scheduling is still handled through phone calls, messages, spreadsheets, or manual records.

This can result in:

* Scheduling conflicts.
* Duplicate information.
* Loss of traceability.
* Difficulty controlling availability for each doctor or therapist.
* Risk of unauthorized access to information.
* Increased operational workload for administrative staff.

PiedraAzul transforms this process into a centralized digital scheduling system with authentication, role-based authorization, availability management, appointment lifecycle control, and traceability.

---

## Value Proposition

PiedraAzul provides more than basic appointment creation.

The system supports the complete appointment workflow through:

* Role-based access.
* Doctor and therapist management.
* Configurable medical availability.
* Dynamic appointment time slots.
* Patient self-service scheduling.
* Scheduler-assisted appointment management.
* Appointment rescheduling.
* Appointment cancellation.
* Appointment status transitions.
* Appointment history.
* CSV schedule export.
* Internal notification processing.
* Optional email notifications.

The main quality attributes considered are:

* **Security:** authentication with Keycloak, JWT tokens, and role-based authorization.
* **Usability:** role-specific web interfaces and structured workflows.
* **Modifiability:** modular organization, ports and adapters, and separation between domain, application, and infrastructure concerns.
* **Traceability:** appointment changes and observations are preserved as part of the appointment history.

---

## Technologies Used

### Frontend

* Angular
* TypeScript
* HTML
* SCSS
* Keycloak JS

### Backend

* Java 17
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Spring Mail
* Maven

### Infrastructure

* MariaDB
* Keycloak
* JWT
* Docker
* Docker Compose

### Architecture and Design

* Modular Monolith
* Hexagonal Architecture
* Ports and Adapters
* Factory Pattern
* Observer Pattern
* Builder Pattern
* Adapter Pattern

## General Architecture

The project uses a web frontend and a single Spring Boot backend organized as a modular monolith.

PiedraAzul
|
├── piedraazul-web/
|   └── Angular web application
|
├── piedraazul-agenda-service/
|   └── Spring Boot modular monolith
|
├── keycloak/
|   └── Keycloak realm configuration
|
├── docs/
|   └── Project documentation and evidence
|
└── docker-compose.yml

The application does not use separate business microservices.

The backend functionality is contained inside a single deployable Spring Boot application.

Modular Monolith

The backend is located in:

piedraazul-agenda-service

Its main package is:

co.edu.unicauca.piedraazul.agenda

The system is divided into functional modules such as:

agenda
├── identity
├── doctors
├── patients
├── appointments
├── availability
├── notifications
└── shared

Each module owns a specific business responsibility while remaining part of the same Spring Boot application and runtime process.

This approach provides separation between business areas without introducing the operational complexity of distributed microservices.

Backend Modules
Identity

Responsible for authentication-related application logic and synchronization with Keycloak.

Main responsibilities include:

User management.
Authentication support.
Role management.
Password operations.
Keycloak integration.

Supported application roles:

ADMIN
SCHEDULER
DOCTOR
PATIENT
Doctors

Responsible for doctor and therapist management.

Supported operations include:

Registering doctors.
Listing doctors.
Editing doctor information.
Removing doctors.
Associating doctors with authentication users.
Managing appointment duration.
Patients

Responsible for patient information and patient-related queries.

Patient information is used during appointment creation and history visualization.

# Availability

Responsible for configuring and calculating doctor availability.

Administrators can configure:

Day of the week.
Start time.
End time.
Appointment interval.
Scheduling window.

The system dynamically calculates the available appointment slots according to:

Doctor configuration.
Selected date.
Appointment interval.
Existing appointments.
Scheduling rules.

Example:

Doctor schedule:
13:00 - 15:00

Interval:
30 minutes

Generated slots:
13:00
13:30
14:00
14:30

An already occupied slot is automatically removed from the available schedule.

Appointments

Responsible for the complete appointment lifecycle.

Supported operations include:

Appointment creation.
Appointment lookup.
Appointment confirmation.
Appointment cancellation.
Appointment rescheduling.
Marking an appointment as attended.
Appointment history.
Doctor schedule lookup.
Patient appointment history.
CSV schedule export.

Typical appointment lifecycle:

PROGRAMADA
    |
    v
CONFIRMADA
    |
    v
ATENDIDA

An appointment can also transition to:

CANCELADA

depending on the applicable business rules.

Notifications

Notifications are implemented as an internal backend module.

There is no independent notification microservice.

The module contains its own:

Domain model.
Application service.
Output ports.
Persistence adapter.
Email adapter integration.

Notification processing therefore occurs inside the modular monolith.

Optional email delivery is supported through Spring Mail.

Email sending can be controlled through configuration such as:

NOTIFICATION_MAIL_ENABLED
NOTIFICATION_MAIL_FROM
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
Hexagonal Architecture

The backend applies hexagonal architecture to isolate business logic from infrastructure concerns.

The modules are organized around concepts such as:

Domain

Contains business models and core rules.

# Application

Contains application services and use cases.

Input Ports

Define operations exposed by the application.

Output Ports

Define dependencies required by application logic.

Examples include:

Persistence operations.
Email sending.
Keycloak operations.
Input Adapters

REST controllers expose application functionality through HTTP endpoints.

Output Adapters

Infrastructure adapters implement external concerns such as:

MariaDB persistence.
Keycloak communication.
Email delivery.

The objective is to prevent the core business logic from depending directly on HTTP controllers, database implementations, or other infrastructure technologies.

# Frontend

The frontend is located in:

piedraazul-web

It is implemented with Angular.

The application provides dedicated portals according to the authenticated user's role.

# Administrator Portal

# Administrators can:

List registered doctors.
Register doctors.
Edit doctor information.
Delete doctors.
Configure doctor availability.
Create availability schedules.
Modify availability schedules.
Configure appointment intervals.
Configure scheduling windows.

Example availability configuration:

Monday
09:00 - 12:00
15-minute interval
4-week scheduling window
Scheduler Portal

# Schedulers can:

Select a doctor or therapist.
Select a date.
View the doctor's schedule.
View appointment information.
Reschedule appointments.
Cancel appointments.
Export schedules to CSV.

CSV exports contain appointment information such as:

ID Appointment
Patient
Doctor
Date
Time
Status
Notes
Doctor Portal

# Doctors can:

View their own profile.
Select a date.
View appointments assigned to them.
Confirm scheduled appointments.
Mark confirmed appointments as attended.
Cancel eligible appointments.
Add observations during status changes.

Example status flow:

PROGRAMADA
    ↓
CONFIRMADA
    ↓
ATENDIDA

Once an appointment reaches a final state, actions that are no longer valid are disabled.

# Patient Portal

Patients can:

View their profile information.
Select a doctor or therapist.
Select an appointment date.
Consult available time slots.
Schedule appointments.
View appointment history.
Reschedule eligible appointments.
Cancel eligible appointments.
View appointment status changes.

When an appointment occupies a time slot, that slot is automatically removed from the availability displayed to patients.
Authentication and Authorization
Authentication is handled by Keycloak.
The frontend redirects the user to Keycloak for login.
The general authentication flow is:

Angular
   |
   v
Keycloak Login
   |
   v
JWT Access Token
   |
   v
Angular
   |
   | Authorization: Bearer <token>
   v
Spring Boot
   |
   v
Role-based authorization

The Spring Boot backend operates as an OAuth2 Resource Server and validates JWT tokens issued by Keycloak.
Application roles are obtained from the Keycloak token.
---Security
Backend endpoints are protected according to the authenticated user's role.

Examples:
Doctor management is restricted to administrators.
Availability configuration is restricted to administrators.
Doctor information can be consulted by authorized application roles.
Appointment endpoints are available only to authenticated roles according to their workflow.
Schedule export is restricted to authorized administrative, scheduler, and doctor roles.
The frontend also applies route guards to prevent users from navigating to portals that do not correspond to their role.

# Business Validations

The system contains business rules including:

Doctor availability validation.
Appointment interval validation.
Scheduling window validation.
Existing appointment validation.
Appointment status transition validation.
Active doctor validation.
Time-slot occupation validation.
Appointment rescheduling validation.
Appointment history registration.
Role-based access validation.
Appointment Traceability
Changes to appointments preserve relevant information in the appointment observations/history.
For example:
Cambio de estado de PROGRAMADA a CONFIRMADA.
Observación: Consulta General confirmada.
and:
Cambio de estado de CONFIRMADA a ATENDIDA.
Observación: Consulta realizada correctamente.
Rescheduling also records the previous and new appointment date/time together with the responsible actor and reason.

# Running the Project

The application requires:
Java 17
Maven
Node.js
npm
Docker
Docker Compose

1. Start Infrastructure and Backend
From the root directory:
docker compose up --build -d

Docker Compose starts:
MariaDB
Keycloak

# PiedraAzul Agenda Service
Check the running containers:
docker compose ps

View backend logs:
docker compose logs -f agenda-service

2. Run the Angular Frontend
Open another terminal:
cd piedraazul-web

Install dependencies when required:
npm install

Start the development server:
npm start

The application is available at:
http://localhost:4200
Local Ports
Component	Port
Angular	4200
Agenda Service	8081
Keycloak	8085
MariaDB	3307
Keycloak
The realm configuration is stored in:
keycloak/piedrazul-realm.json
Docker Compose imports this realm when Keycloak starts.
The configured realm is:
PiedrAzul
The Angular client is:
piedraazul-spa
Test Users
The development environment includes users for the main application roles.

Role	Username
Administrator	admin
Scheduler	scheduler
Doctor	doctor
Patient	patient
Passwords are intended only for the local academic/development environment and should not be reused in production environments.

# Backend Tests
The backend test suite can be executed from:

cd piedraazul-agenda-service
Ensure Java 17 is active.
Then run:
mvn test
Current validated result:
Tests run: 85
Failures: 0
Errors: 0
Skipped: 0
Frontend TypeScript Validation
From:
cd piedraazul-web
run:
npx tsc -p tsconfig.app.json --noEmit
A successful validation returns to the terminal without TypeScript compilation errors.

# Docker Commands
Start
docker compose up --build -d
View containers
docker compose ps
View backend logs
docker compose logs -f agenda-service
Stop
docker compose down
Stop and remove local volumes
docker compose down -v

Use the last command carefully because it removes the local MariaDB and Keycloak persistent volumes.

# Current Project Structure
PA/
├── .github/
├── .vscode/
├── docs/
├── keycloak/
│   └── piedrazul-realm.json
├── piedraazul-agenda-service/
├── piedraazul-web/
├── .gitignore
├── docker-compose.yml
└── README.md

Legacy components based on JavaFX and the previous standalone notification service are no longer part of the current architecture.

# Architectural Decision
PiedraAzul uses a modular monolith instead of independent business microservices.
This means:

There is a single Spring Boot backend application.
Business capabilities remain separated into modules.
Modules communicate inside the same application process.
There are no HTTP calls between internal business modules.
There is no RabbitMQ or Kafka dependency between business modules.
Notifications are processed internally.
A single backend deployment contains the application business logic.

This architecture preserves modularity while reducing the deployment and communication complexity associated with distributed microservices.

# Project Status
The current application includes:

Angular web frontend.
Spring Boot modular monolith.
MariaDB persistence.
Keycloak authentication.
JWT authorization.
Role-based access control.
Doctor management.
Patient management.
Availability management.
Appointment scheduling.
Appointment cancellation.
Appointment rescheduling.
Appointment status management.
Appointment history and traceability.
CSV schedule export.
Internal notification processing.
Optional email notification support.
Automated backend tests.
Product Value

# Its main value includes:

Reducing scheduling errors.
Saving administrative time.
Improving the patient experience.
Controlling doctor and therapist availability.
Protecting access to information.
Preserving appointment traceability.
Providing clear separation of responsibilities through modular architecture.
Facilitating future system evolution.
Authors

# Development team:

.ING Charry Vela Brayan
.ING Puentes Figueroa Jhoiner
.ING Majé Bonilla Santiago
.ING Narvaez Canchala Julian
