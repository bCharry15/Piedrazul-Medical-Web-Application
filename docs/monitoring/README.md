# Monitoreo de PiedraAzul

## Qué puede demostrar el equipo

En el portal de administrador, abrir **Monitoreo del sistema** en el menú lateral.
La sección muestra:

- Estado general del backend, obtenido de Spring Boot Actuator.
- Estado de la conexión a la base de datos de la agenda.
- Tiempo activo del proceso Java, memoria JVM utilizada y solicitudes HTTP completadas.
- Identificación de Keycloak como proveedor de la sesión centralizada existente.
- Últimos 32 KiB del archivo de logs, limitados a 100 líneas en la vista.

El estado general incluye los indicadores de base de datos y espacio libre en disco.
No comprueba la disponibilidad del frontend ni de Keycloak en tiempo real. La tarjeta
de Keycloak identifica la integración existente; no se presenta como un sondeo de salud.
El correo es opcional y no forma parte de la salud de la agenda.

Los datos se consultan al abrir el panel y al pulsar **Actualizar estado**.
Los logs se solicitan únicamente al pulsar **Consultar logs**. La fecha de consulta
permite distinguir estos datos de una transmisión continua.

## Rutas

| Ruta | Acceso | Resultado |
|---|---|---|
| `GET /api/agenda/health` | Público | `{"status":"UP"}` y HTTP 200, o estado no operativo y HTTP 503. |
| `GET /api/monitoring/health` | ADMIN | Estado y componentes, incluida la base de datos. |
| `GET /api/monitoring/metrics/process.uptime` | ADMIN | Segundos desde el inicio del proceso. |
| `GET /api/monitoring/metrics/jvm.memory.used` | ADMIN | Memoria JVM utilizada, en bytes. |
| `GET /api/monitoring/metrics/http.server.requests` | ADMIN | Solicitudes completadas y estadísticas HTTP. |
| `GET /api/monitoring/logfile` | ADMIN | Archivo de logs; el panel solicita un rango de 32 KiB. |

El monitoreo es de solo lectura. Solicitudes sin sesión reciben HTTP 401; pacientes,
médicos y agendadores reciben HTTP 403. Los métodos de escritura sobre estas rutas
se rechazan incluso para ADMIN. No se publican endpoints de variables de entorno,
volcados de memoria ni cambios de configuración de loggers.

El endpoint público conserva su URL, utilizada por el despliegue continuo, pero ahora
su respuesta es JSON y comprueba dependencias. Antes devolvía un texto fijo. Un fallo
de conexión con la base de datos cambia el estado a DOWN en lugar de informar éxito.

## Logs

Cada solicitud añade una línea con fecha, nivel, identificador de solicitud, método,
plantilla de ruta, código HTTP y duración. Por ejemplo:

```text
INFO [requestId=...] HTTP method=GET route=/api/appointments status=200 durationMs=12
WARN [requestId=...] HTTP method=GET route=unmatched status=401 durationMs=2
```

El filtro nuevo no registra cuerpos, tokens, contraseñas, parámetros de consulta ni
identificadores de pacientes incluidos en la URL. Usa plantillas de rutas como
`/api/appointments/patient/{documentNumber}`. Las solicitudes rechazadas antes de
identificar un controlador se muestran como `unmatched`.

Estos registros operativos no sustituyen el historial funcional de citas ni una
auditoría de inicios de sesión de Keycloak. El archivo puede contener mensajes
emitidos por otras partes del backend, por lo que su acceso se limita a ADMIN.
La vista usa texto plano; no interpreta HTML incluido en un log.

El archivo se escribe en `/app/logs/piedraazul-agenda.log` dentro del contenedor.
El volumen `agenda_logs` conserva estos logs al reconstruir el contenedor. La
rotación usa archivos de hasta 10 MB, un historial de siete periodos diarios y
un límite de archivos históricos de 100 MB. El log actual se suma a ese límite.
También se limita la rotación del log de consola Docker del backend a tres archivos
de 10 MB. El contador de solicitudes y las métricas se reinician con el proceso.

Para ver en terminal la salida del backend y de Keycloak:

```powershell
docker compose logs --tail=100 -f agenda-service keycloak
```

En Azure, desde la carpeta del proyecto:

```bash
docker compose -f docker-compose.prod.yml --env-file .env logs --tail=100 -f agenda-service keycloak
```

## Instalación en la rama charry-3E

El parche se preparó sobre el commit `c81dbd7`, que contiene la conexión del frontend
local con Azure. Desde `C:\Piedrazul-charry`, con los archivos guardados y sin cambios
pendientes:

```powershell
git status
git apply --check "RUTA\Piedrazul_Monitoreo.patch"
git apply "RUTA\Piedrazul_Monitoreo.patch"
```

Sustituir RUTA por la carpeta donde se extrajo el ZIP. Si `--check` informa que un
parche no aplica, no forzarlo: revisar las diferencias de la versión del repositorio.
No es necesario restaurar el stash temporal del cambio anterior.

Validar y revisar:

```powershell
cd piedraazul-agenda-service
mvn test
cd ..\piedraazul-web
npm ci
npm run build
npm test -- --watch=false
cd ..
git diff --check
git status
```

El backend requiere un JDK 17. Los comandos npm requieren una versión de Node
compatible con las dependencias Angular del proyecto.

Para registrar los cambios:

```powershell
git add .gitignore docker-compose.yml docker-compose.prod.yml docs/monitoring piedraazul-agenda-service/pom.xml piedraazul-agenda-service/src/main piedraazul-agenda-service/src/test piedraazul-web/src/app/features/admin
git commit -m "feat(monitoring): agrega salud, metricas y logs al portal administrador"
git push origin charry-3E
```

## Publicación en Azure: necesaria para verlo desde localhost

El frontend local usa el backend de Azure. Por eso aplicar el parche solo al frontend
no habilita las rutas nuevas en el servidor remoto: el panel informa que la función
no está desplegada si el backend devuelve HTTP 404.

1. Subir el commit a `charry-3E`.
2. Crear y revisar un Pull Request de `charry-3E` hacia `main`.
3. Cuando las pruebas pasen, integrar el PR y esperar el despliegue CD de Azure.
4. Volver a abrir el portal ADMIN en Azure o el frontend local y consultar el panel.

La configuración de Caddy ya dirige `/api/*` al backend; las rutas de monitoreo usan
ese mismo prefijo y no necesitan publicar puertos nuevos. La configuración de CORS
permite el encabezado Range utilizado por el visor local de logs.

El despliegue normal crea el volumen de logs sin reemplazar los volúmenes existentes
de MariaDB y Keycloak. No hace falta ejecutar `docker compose down -v`.

## Guion breve para la presentación

1. Iniciar sesión como ADMIN mediante Keycloak.
2. Abrir Monitoreo y mostrar backend, base de datos y hora de consulta.
3. Consultar una agenda, volver al panel y actualizar las métricas.
4. Pulsar Consultar logs y señalar una ruta, su código HTTP y su duración.
5. Explicar que los cuatro roles se autentican con el mismo Keycloak y que solo
   ADMIN tiene permiso para consultar el monitoreo.

El panel no incluye alertas automáticas ni almacenamiento histórico de métricas.
No se necesita detener la base de datos de Azure para la demostración: el caso de
salud DOWN se cubre en las pruebas automatizadas.

Referencia técnica: https://docs.spring.io/spring-boot/docs/3.2.x/reference/html/actuator.html
