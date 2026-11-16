# PiedraAzul · Diagrama de contexto C4

PiedraAzul es un sistema web para gestionar citas médicas, consultar la agenda y configurar la disponibilidad de los profesionales.

```mermaid
flowchart LR
    patient["Paciente"] -->|Reserva y consulta sus citas| system["PiedraAzul · Sistema web de citas"]
    scheduler["Agendador"] -->|Gestiona citas y consulta la agenda| system
    doctor["Médico o terapeuta"] -->|Consulta su agenda y sus reportes| system
    admin["Administrador"] -->|Configura la disponibilidad| system
    system -->|Autenticación y gestión de identidades| keycloak["Keycloak"]
```

El médico consulta la cantidad de citas por mes para un año elegido y la asistencia por estado para una semana. Ambos reportes usan el perfil del médico autenticado.