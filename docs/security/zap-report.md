# Informe de seguridad – OWASP ZAP (revisión pasiva)

**Proyecto:** Piedrazul Medical Web Application
**Rama:** `majé-3E`
**Fecha de ejecución:** 25 de noviembre de 2026
**Herramienta:** OWASP ZAP 2.17.0 (`ghcr.io/zaproxy/zaproxy:stable`, Docker)
**Tipo de revisión:** pasiva (spider + reglas pasivas). No se ejecutó escaneo activo.

## 1. Alcance

| Componente | Objetivo escaneado | Script de ZAP | Evidencia |
|---|---|---|---|
| Frontend Angular (servidor de desarrollo) | `http://192.168.65.254:4200` | `zap-baseline.py` | `docs/evidence/security/zap-web.html` / `.json` |
| API Spring Boot (`agenda-service`) | `http://host.docker.internal:8081/v3/api-docs` (OpenAPI) | `zap-api-scan.py -S` (modo seguro) | `docs/evidence/security/zap-api.html` / `.json` |
| Keycloak 25.0.6 (realm `PiedrAzul`) | `http://host.docker.internal:8085/realms/PiedrAzul/account` | `zap-baseline.py` | `docs/evidence/security/zap-keycloak.html` / `.json` |

El entorno de pruebas es el definido en `docker-compose.yml` (MariaDB, Keycloak y `agenda-service`) más el frontend ejecutado con `ng serve`.

## 2. Procedimiento

ZAP se ejecutó en contenedores temporales (`--rm`), sin usarlo como proxy del navegador y **sin modificar código fuente ni configuraciones** de Angular, Spring Boot, Keycloak o CORS. Los servicios del host se alcanzaron desde el contenedor mediante `host.docker.internal`.

```powershell
# Carpeta de evidencias
$ev = "$PWD\docs\evidence\security"

# API: importa el OpenAPI y aplica solo reglas pasivas (-S omite el escaneo activo)
docker run --rm -v "${ev}:/zap/wrk/:rw" -t ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py `
  -t http://host.docker.internal:8081/v3/api-docs -f openapi -O http://host.docker.internal:8081 -S `
  -r zap-api.html -J zap-api.json -I

# Keycloak
docker run --rm -v "${ev}:/zap/wrk/:rw" -t ghcr.io/zaproxy/zaproxy:stable zap-baseline.py `
  -t http://host.docker.internal:8085/realms/PiedrAzul/account -r zap-keycloak.html -J zap-keycloak.json -I

# Frontend: Angular iniciado con `npx ng serve --host 0.0.0.0` (parámetro temporal, sin cambiar angular.json)
# y escaneado por la IPv4 de host.docker.internal, porque el servidor de desarrollo rechaza (403) otros nombres de host.
docker run --rm -v "${ev}:/zap/wrk/:rw" -t ghcr.io/zaproxy/zaproxy:stable zap-baseline.py `
  -t http://192.168.65.254:4200 -r zap-web.html -J zap-web.json -I
```

## 3. Resumen de resultados

| Componente | URLs | FAIL-NEW | WARN-NEW | PASS |
|---|---:|---:|---:|---:|
| Frontend Angular | 9 | 0 | 5 | 63 |
| API Spring Boot | 50 | 0 | 2 | 118* |
| Keycloak | 30 | 0 | 6 | 62 |

\* En modo seguro (`-S`) ZAP no ejecuta el escaneo activo, pero sigue listando las reglas activas (SQL Injection, XSS, etc.) como PASS porque no generaron alertas. **Esas reglas no se ejecutaron** y no deben interpretarse como pruebas superadas.

No se registró ninguna alerta de riesgo **Alto**.

## 4. Hallazgos y estado

Estados utilizados:

- **Mitigado:** el control ya existe en el proyecto.
- **Aceptado:** riesgo conocido y asumido, con justificación.
- **Falso positivo:** la alerta no corresponde a un problema real.
- **Pendiente:** requiere un cambio que queda recomendado y fuera del alcance de este commit.
- **Informativo:** sin riesgo; no requiere acción.

### 4.1 Frontend Angular (`:4200`)

| ID | Alerta (plugin) | Riesgo | Estado | Justificación / acción |
|---|---|---|---|---|
| WEB-01 | CSP: `style-src unsafe-inline` (10055) | Medio | Aceptado | El servidor de desarrollo ya envía una CSP (definida en `angular.json` → `serve.options.headers`) con `script-src 'self'`, `frame-ancestors 'none'` y `object-src 'none'`. `unsafe-inline` en estilos es necesario para los estilos de componentes de Angular; eliminarlo exige nonces (`autoCsp`). El riesgo residual es bajo porque los scripts inline siguen bloqueados. |
| WEB-02 | CSP: Failure to Define Directive with No Fallback (10055) | Medio | Falso positivo | Solo aparece en las respuestas 404 de `/robots.txt` y `/sitemap.xml`, que el servidor de desarrollo envía con `default-src 'none'`, la política más restrictiva posible. |
| WEB-03 | Cross-Origin-Embedder/Opener/Resource-Policy ausentes (90004) | Bajo | Pendiente | Cabeceras de aislamiento entre orígenes (Spectre). Se recomienda añadirlas en el servidor que publique el build de producción; no se modifican en el servidor de desarrollo. |
| WEB-04 | Permissions Policy Header Not Set (10063) | Bajo | Pendiente | Añadir `Permissions-Policy` (por ejemplo `camera=(), microphone=(), geolocation=()`) en el servidor de producción. |
| WEB-05 | Modern Web Application (10109) | Informativo | Informativo | Indica que es una SPA; el spider tradicional no ejecuta JavaScript. |
| WEB-06 | Storable / Non-Cacheable Content (10049) | Informativo | Informativo | `index.html` se sirve con `no-cache`, correcto para una SPA. |

Controles verificados como **Mitigados** (reglas PASS): `X-Frame-Options: DENY` (10020), `X-Content-Type-Options: nosniff` (10021), CSP presente (10038), sin `X-Powered-By` (10037) y sin librerías JS vulnerables conocidas (10003).

### 4.2 API Spring Boot (`:8081`)

| ID | Alerta (plugin) | Riesgo | Estado | Justificación / acción |
|---|---|---|---|---|
| API-01 | Cookie without SameSite Attribute – `JSESSIONID` (10054) | Bajo | Pendiente | La API autentica con JWT (Bearer) de Keycloak, pero crea sesión HTTP y emite `JSESSIONID` incluso en respuestas 401. Se recomienda `sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))` en `SecurityConfig`, lo que elimina la cookie. El impacto es bajo porque la autorización depende del token y no de la cookie. |
| API-02 | Cross-Origin-Resource-Policy ausente (90004) | Bajo | Aceptado | Solo en `/v3/api-docs` y `/api/agenda/health`, que son públicos y no contienen datos sensibles. El acceso entre orígenes ya está restringido por la configuración CORS. |
| API-03 | A Client Error response code was returned (100000) | Informativo | Informativo (control positivo) | De 34 respuestas de error, 30 fueron **401** en endpoints protegidos sin token y 4 fueron **400** en los públicos (`login`, `forgot-password`, `reset-password`, `public/patients/register`) ante datos inválidos. Confirma que la autorización y la validación funcionan. |
| API-04 | Authentication Request Identified (10111) | Informativo | Informativo | ZAP identificó un campo `password` en el esquema de `POST /api/doctors`. |
| API-05 | Session Management Response Identified (10112) | Informativo | Informativo | Relacionado con API-01. |
| API-06 | Non-Storable Content (10049) | Informativo | Mitigado | Las respuestas sensibles (`reset-password`, registro, perfil) se envían con `no-store`. |

Al importar el OpenAPI, ZAP envía una petición por cada operación (incluidas `POST`, `PUT` y `DELETE`). **Todas las de escritura fueron rechazadas (401/400)**, por lo que el escaneo no modificó datos.

### 4.3 Keycloak (`:8085`)

| ID | Alerta (plugin) | Riesgo | Estado | Justificación / acción |
|---|---|---|---|---|
| KC-01 | CSP: Wildcard Directive, `script-src`/`style-src unsafe-inline`, Failure to Define Directive (10055) | Medio | Aceptado (componente de terceros) | Corresponde a la CSP por defecto de Keycloak 25 en la consola de cuentas y en la consola de administración, que ya incluye `frame-ancestors 'self'` y `object-src 'none'`. Se puede endurecer en *Realm settings → Security defenses*, pero puede romper las consolas de Keycloak. |
| KC-02 | Cross-Origin-Embedder/Opener/Resource-Policy ausentes (90004) | Bajo | Aceptado (componente de terceros) | Cabeceras no emitidas por Keycloak por defecto; se recomienda añadirlas en un proxy inverso en producción. |
| KC-03 | Permissions Policy Header Not Set (10063) | Bajo | Aceptado (componente de terceros) | Igual que KC-02. |
| KC-04 | Timestamp Disclosure – Unix (10096) | Bajo | Falso positivo | Los valores detectados son constantes numéricas dentro del JavaScript minificado de la consola de cuentas (`main-*.js`), no marcas de tiempo del servidor. |
| KC-05 | Modern Web Application (10109) | Informativo | Informativo | Las consolas de Keycloak son SPA. |
| KC-06 | Storable and Cacheable / Non-Cacheable Content (10049) | Informativo | Informativo | Recursos estáticos y documentos de descubrimiento públicos. |

## 5. Observaciones adicionales (no reportadas como alertas por ZAP)

| ID | Observación | Estado | Acción recomendada |
|---|---|---|---|
| OBS-01 | La consola de administración del realm `master` (`/admin/master/console/`) es accesible en el puerto 8085. | Aceptado (entorno de pruebas) | En producción, no exponer la consola de administración y no usar `start-dev`. |
| OBS-02 | `docker-compose.yml` usa credenciales por defecto (`admin/admin`, `root`) para Keycloak y MariaDB. | Aceptado (entorno de pruebas) | Usar variables de entorno o secretos fuera del repositorio en producción. |
| OBS-03 | Swagger UI y `/v3/api-docs` son públicos. | Aceptado (entorno de pruebas) | Deshabilitarlos en producción (`springdoc.api-docs.enabled=false`). |

## 6. Limitaciones

- **Revisión pasiva y sin autenticación:** las páginas y endpoints que requieren login solo se observaron a través de sus respuestas 401.
- **Spider tradicional sobre una SPA:** en el frontend se descubrieron 9 URLs porque el spider no ejecuta JavaScript ni sigue las rutas de Angular.
- **Servidor de desarrollo:** el frontend se analizó con `ng serve`; las cabeceras de un despliegue de producción pueden ser distintas.
- **Intento descartado:** un primer escaneo del frontend con `host.docker.internal:4200` obtuvo 403 en todas las respuestas (bloqueo de nombre de host del servidor de desarrollo) y se reemplazó por el escaneo por IP incluido como evidencia.

## 7. Conclusión

La revisión pasiva no encontró alertas de riesgo alto ni fallos (`FAIL-NEW = 0`) en los tres componentes. Los hallazgos de riesgo medio corresponden a políticas CSP que ya existen pero no son estrictas (`unsafe-inline`), en el frontend y en Keycloak, y quedan aceptados con justificación. Los de riesgo bajo son cabeceras de endurecimiento recomendadas para producción y la cookie `JSESSIONID` de la API (API-01), que es la única acción de código recomendada y queda pendiente fuera del alcance de este commit.
