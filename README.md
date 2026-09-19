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

---

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

# Authors
ING Charry Vela Brayan
ING Puentes Figueroa Jhoiner
ING Majé Bonilla Santiago
ING Narvaez Canchala Julian
