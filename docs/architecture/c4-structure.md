# PiedraAzul · Estructura C4

## Nivel 2 · Contenedores

```mermaid
flowchart TD
    web["PiedraAzul Web · Angular SPA"]
    api["Servicio de agenda · Spring Boot"]
    db[("Base de agenda · MariaDB")]
    kc["Proveedor de identidad · Keycloak"]

    web -->|Solicitudes REST con JWT| api
    web -->|Inicio de sesión con OpenID Connect| kc
    api -->|Consulta claves públicas y gestiona usuarios| kc
    api -->|Lee y guarda datos mediante JPA| db
```

Angular presenta los portales de paciente, agendador, médico y administrador.
Spring Boot concentra la lógica del negocio en un monolito modular.
MariaDB almacena los datos de la agenda y Keycloak administra las identidades y los roles.

## Nivel 3 · Componentes de citas y reportes

```mermaid
flowchart TD
    subgraph agenda["Servicio de agenda · Módulo de citas"]
        appointments["AppointmentRestController"]
        reports["MonthlyAppointmentReportController"]
        usecases["Casos de uso de citas"]
        statistics["Servicios mensual y semanal"]
        repository["AppointmentRepository"]

        appointments --> usecases
        reports --> statistics
        usecases -->|Mediante puertos y adaptadores de persistencia| repository
        statistics -->|Consultas agregadas| repository
    end

    repository --> db[("MariaDB")]
```

El controlador de reportes expone las rutas mensual y semanal. Identifica al
médico activo mediante la sesión autenticada y entrega su identificador al
servicio correspondiente.