/*
 * Configuración de producción.
 *
 * En el entorno publicado el frontend, la API y Keycloak se sirven
 * desde el mismo dominio (proxy inverso): la API en /api y Keycloak en /auth.
 */
export const environment = {
  production: true,
  apiUrl: window.location.origin,
  keycloakUrl: `${window.location.origin}/auth`,
};
