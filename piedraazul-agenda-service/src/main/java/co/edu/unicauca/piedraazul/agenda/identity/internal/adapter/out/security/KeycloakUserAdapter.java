package co.edu.unicauca.piedraazul.agenda.identity.internal.adapter.out.security;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.RegisterUserKeycloakPort;

@Component
public class KeycloakUserAdapter implements RegisterUserKeycloakPort {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-token-uri}")
    private String adminTokenUri;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Override
    public void registerUser(
            String username,
            String password,
            String role
    ) {
        try {

            String normalizedUsername =
                    validateText(
                            username,
                            "El nombre de usuario es obligatorio."
                    );

            String normalizedPassword =
                    validateText(
                            password,
                            "La contraseña es obligatoria."
                    );

            String normalizedRole =
                    validateText(
                            role,
                            "El rol es obligatorio."
                    ).toUpperCase();

            String adminToken =
                    getAdminToken();

            createUserIfAbsent(
                    normalizedUsername,
                    adminToken
            );

            String userId =
                    getUserId(
                            normalizedUsername,
                            adminToken
                    );

            enableUserAndClearPendingActions(
                    userId,
                    normalizedUsername,
                    adminToken
            );

            updatePasswordById(
                    userId,
                    normalizedPassword,
                    false,
                    adminToken
            );

            assignRoleToUser(
                    userId,
                    normalizedRole,
                    adminToken
            );

            System.out.println(
                    "AGENDA-SERVICE -> Usuario sincronizado con Keycloak: "
                            + normalizedUsername
                            + " / rol: "
                            + normalizedRole
            );

        } catch (Exception ex) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible sincronizar el usuario con Keycloak: "
                            + getFullMessage(ex),
                    ex
            );
        }
    }

    @Override
    public void updatePassword(
            String username,
            String newPassword,
            boolean temporary
    ) {
        try {

            String normalizedUsername =
                    validateText(
                            username,
                            "El nombre de usuario es obligatorio."
                    );

            String normalizedPassword =
                    validateText(
                            newPassword,
                            "La nueva contraseña es obligatoria."
                    );

            String adminToken =
                    getAdminToken();

            String userId =
                    getUserId(
                            normalizedUsername,
                            adminToken
                    );

            enableUserAndClearPendingActions(
                    userId,
                    normalizedUsername,
                    adminToken
            );

            updatePasswordById(
                    userId,
                    normalizedPassword,
                    temporary,
                    adminToken
            );

            System.out.println(
                    "AGENDA-SERVICE -> Password actualizada en Keycloak para: "
                            + normalizedUsername
            );

        } catch (Exception ex) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible actualizar la contraseña en Keycloak: "
                            + getFullMessage(ex),
                    ex
            );
        }
    }

    @Override
    public void disableUser(String username) {
        try {

            String normalizedUsername =
                    validateText(
                            username,
                            "El nombre de usuario es obligatorio."
                    );

            String adminToken =
                    getAdminToken();

            String userId =
                    getUserId(
                            normalizedUsername,
                            adminToken
                    );

            disableUserById(
                    userId,
                    normalizedUsername,
                    adminToken
            );

            System.out.println(
                    "AGENDA-SERVICE -> Usuario deshabilitado en Keycloak: "
                            + normalizedUsername
            );

        } catch (Exception ex) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible deshabilitar el usuario en Keycloak: "
                            + getFullMessage(ex),
                    ex
            );
        }
    }

    private String getAdminToken() {

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        String body =
                "grant_type=password"
                        + "&client_id=admin-cli"
                        + "&username="
                        + URLEncoder.encode(
                                adminUsername,
                                StandardCharsets.UTF_8
                        )
                        + "&password="
                        + URLEncoder.encode(
                                adminPassword,
                                StandardCharsets.UTF_8
                        );

        HttpEntity<String> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        adminTokenUri,
                        HttpMethod.POST,
                        request,
                        Map.class
                );

        if (
                response.getBody() == null
                        || response
                        .getBody()
                        .get("access_token") == null
        ) {

            throw new IllegalStateException(
                    "Keycloak no retornó access_token de administrador."
            );
        }

        return response
                .getBody()
                .get("access_token")
                .toString();
    }

    private void createUserIfAbsent(
            String username,
            String adminToken
    ) {

        String url =
                keycloakBaseUrl
                        + "/admin/realms/"
                        + realm
                        + "/users";

        HttpHeaders headers =
                createHeadersAdmin(
                        adminToken
                );

        Map<String, Object> body =
                createCompleteUserBody(
                        username
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        try {

            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            System.out.println(
                    "AGENDA-SERVICE -> Usuario creado en Keycloak: "
                            + username
            );

        } catch (
                HttpClientErrorException.Conflict ex
        ) {

            System.out.println(
                    "AGENDA-SERVICE -> Usuario ya existía en Keycloak: "
                            + username
            );
        }
    }

    private String getUserId(
            String username,
            String adminToken
    ) {

        String encodedUsername =
                URLEncoder.encode(
                        username,
                        StandardCharsets.UTF_8
                );

        String url =
                keycloakBaseUrl
                        + "/admin/realms/"
                        + realm
                        + "/users?username="
                        + encodedUsername
                        + "&exact=true";

        HttpHeaders headers =
                createHeadersAdmin(
                        adminToken
                );

        HttpEntity<Void> request =
                new HttpEntity<>(
                        headers
                );

        ResponseEntity<List> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        List.class
                );

        if (
                response.getBody() == null
                        || response
                        .getBody()
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "No se encontró el usuario en Keycloak: "
                            + username
            );
        }

        Map user =
                (Map) response
                        .getBody()
                        .get(0);

        if (user.get("id") == null) {

            throw new IllegalStateException(
                    "Keycloak no retornó id para el usuario: "
                            + username
            );
        }

        return user
                .get("id")
                .toString();
    }

    private void enableUserAndClearPendingActions(
            String userId,
            String username,
            String adminToken
    ) {

        String url =
                keycloakBaseUrl
                        + "/admin/realms/"
                        + realm
                        + "/users/"
                        + userId;

        HttpHeaders headers =
                createHeadersAdmin(
                        adminToken
                );

        Map<String, Object> body =
                createCompleteUserBody(
                        username
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
        );
    }

    private void disableUserById(
            String userId,
            String username,
            String adminToken
    ) {

        String url =
                keycloakBaseUrl
                        + "/admin/realms/"
                        + realm
                        + "/users/"
                        + userId;

        HttpHeaders headers =
                createHeadersAdmin(
                        adminToken
                );

        Map<String, Object> body =
                Map.of(
                        "username",
                        username,
                        "enabled",
                        false
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
        );
    }

    private Map<String, Object>
            createCompleteUserBody(
                    String username
            ) {

        String cleanUsername =
                username.trim();

        return Map.of(
                "username",
                cleanUsername,

                "enabled",
                true,

                "emailVerified",
                true,

                "firstName",
                cleanUsername,

                "lastName",
                "PiedraAzul",

                "email",
                createSafeEmail(
                        cleanUsername
                ),

                "requiredActions",
                List.of()
        );
    }

    private String createSafeEmail(
            String username
    ) {

        String localPart =
                username
                        .toLowerCase()
                        .replaceAll(
                                "[^a-z0-9._-]",
                                "."
                        );

        localPart =
                localPart
                        .replaceAll(
                                "\\.+",
                                "."
                        )
                        .replaceAll(
                                "^\\.|\\.$",
                                ""
                        );

        if (localPart.isBlank()) {
            localPart = "user";
        }

        return localPart
                + "@piedraazul.local";
    }

    private void updatePasswordById(
            String userId,
            String newPassword,
            boolean temporary,
            String adminToken
    ) {

        String url =
                keycloakBaseUrl
                        + "/admin/realms/"
                        + realm
                        + "/users/"
                        + userId
                        + "/reset-password";

        HttpHeaders headers =
                createHeadersAdmin(
                        adminToken
                );

        Map<String, Object> credential =
                Map.of(
                        "type",
                        "password",

                        "value",
                        newPassword,

                        "temporary",
                        temporary
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        credential,
                        headers
                );

        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
        );
    }

    private void assignRoleToUser(
            String userId,
            String role,
            String adminToken
    ) {

        Map keycloakRole =
                getRoleRepresentation(
                        role,
                        adminToken
                );

        String url =
                keycloakBaseUrl
                        + "/admin/realms/"
                        + realm
                        + "/users/"
                        + userId
                        + "/role-mappings/realm";

        HttpHeaders headers =
                createHeadersAdmin(
                        adminToken
                );

        HttpEntity<List<Map>> request =
                new HttpEntity<>(
                        List.of(
                                keycloakRole
                        ),
                        headers
                );

        try {

            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Void.class
            );

        } catch (
                HttpClientErrorException.Conflict ex
        ) {

            System.out.println(
                    "AGENDA-SERVICE -> El usuario ya tenía asignado el rol: "
                            + role
            );
        }
    }

    private Map getRoleRepresentation(
            String role,
            String adminToken
    ) {

        String encodedRole =
                URLEncoder.encode(
                        role,
                        StandardCharsets.UTF_8
                );

        String url =
                keycloakBaseUrl
                        + "/admin/realms/"
                        + realm
                        + "/roles/"
                        + encodedRole;

        HttpHeaders headers =
                createHeadersAdmin(
                        adminToken
                );

        HttpEntity<Void> request =
                new HttpEntity<>(
                        headers
                );

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        Map.class
                );

        if (response.getBody() == null) {

            throw new IllegalStateException(
                    "No existe el rol en Keycloak: "
                            + role
            );
        }

        return response.getBody();
    }

    private HttpHeaders createHeadersAdmin(
            String adminToken
    ) {

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setBearerAuth(
                adminToken
        );

        return headers;
    }

    private String validateText(
            String value,
            String message
    ) {

        if (
                value == null
                        || value
                        .trim()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return value.trim();
    }

    private String getFullMessage(
            Exception ex
    ) {

        if (ex == null) {
            return "";
        }

        StringBuilder messageBuilder =
                new StringBuilder();

        Throwable current =
                ex;

        while (current != null) {

            if (
                    current.getMessage()
                            != null
            ) {

                messageBuilder
                        .append(
                                current.getMessage()
                        )
                        .append(" | ");
            }

            current =
                    current.getCause();
        }

        return messageBuilder.toString();
    }
}