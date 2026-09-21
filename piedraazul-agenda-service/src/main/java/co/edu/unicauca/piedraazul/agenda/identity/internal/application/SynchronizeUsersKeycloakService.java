package co.edu.unicauca.piedraazul.agenda.identity.internal.application;

import org.springframework.stereotype.Service;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.RegisterUserKeycloakPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;

@Service
public class SynchronizeUsersKeycloakService {

    public static final String RECOVERY_PASSWORD =
            "PiedraAzul123";

    private static final int MAX_ATTEMPTS =
            10;

    private static final long RETRY_DELAY_MS =
            3000;

    private final RegisterUserKeycloakPort
            registerUserKeycloakPort;

    public SynchronizeUsersKeycloakService(
            RegisterUserKeycloakPort registerUserKeycloakPort
    ) {

        this.registerUserKeycloakPort =
                registerUserKeycloakPort;
    }

    public void synchronizeUserRequired(
            String username,
            String password,
            UserRole role
    ) {

        synchronizeUser(
                username,
                password,
                role,
                true
        );
    }

    public void synchronizeUserOnStartup(
            String username,
            String password,
            UserRole role
    ) {

        synchronizeUser(
                username,
                password,
                role,
                false
        );
    }

    public void disableUserRequired(
            String username
    ) {

        if (
                username == null
                        || username
                        .trim()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio para deshabilitarlo en Keycloak."
            );
        }

        String normalizedUsername =
                username.trim();

        Exception lastError =
                null;

        for (
                int attempt = 1;
                attempt <= MAX_ATTEMPTS;
                attempt++
        ) {

            try {

                registerUserKeycloakPort
                        .disableUser(
                                normalizedUsername
                        );

                System.out.println(
                        "AGENDA-SERVICE -> Usuario deshabilitado correctamente en Keycloak: "
                                + normalizedUsername
                );

                return;

            } catch (Exception ex) {

                lastError =
                        ex;

                System.out.println(
                        "AGENDA-SERVICE -> Intento "
                                + attempt
                                + "/"
                                + MAX_ATTEMPTS
                                + " falló deshabilitando usuario en Keycloak: "
                                + normalizedUsername
                                + ". Detalle: "
                                + getMessage(ex)
                );

                waitBeforeRetry();
            }
        }

        throw new IllegalStateException(
                "No se pudo deshabilitar el usuario en Keycloak después de "
                        + MAX_ATTEMPTS
                        + " intentos: "
                        + normalizedUsername
                        + ". Detalle: "
                        + getMessage(lastError),
                lastError
        );
    }

    private void synchronizeUser(
            String username,
            String password,
            UserRole role,
            boolean required
    ) {

        validateUserData(
                username,
                password,
                role
        );

        String normalizedUsername =
                username.trim();

        String normalizedPassword =
                password.trim();

        Exception lastError =
                null;

        for (
                int attempt = 1;
                attempt <= MAX_ATTEMPTS;
                attempt++
        ) {

            try {

                registerUserKeycloakPort
                        .registerUser(
                                normalizedUsername,
                                normalizedPassword,
                                role.name()
                        );

                System.out.println(
                        "AGENDA-SERVICE -> Usuario sincronizado con Keycloak: "
                                + normalizedUsername
                                + " / rol: "
                                + role.name()
                );

                return;

            } catch (Exception ex) {

                lastError =
                        ex;

                if (
                        isConflictForExistingUser(
                                ex
                        )
                ) {

                    updatePasswordForExistingUser(
                            normalizedUsername,
                            normalizedPassword
                    );

                    return;
                }

                System.out.println(
                        "AGENDA-SERVICE -> Intento "
                                + attempt
                                + "/"
                                + MAX_ATTEMPTS
                                + " falló sincronizando usuario con Keycloak: "
                                + normalizedUsername
                                + ". Detalle: "
                                + getMessage(ex)
                );

                waitBeforeRetry();
            }
        }

        String message =
                "No se pudo sincronizar el usuario con Keycloak después de "
                        + MAX_ATTEMPTS
                        + " intentos: "
                        + normalizedUsername
                        + ". Detalle: "
                        + getMessage(lastError);

        if (required) {

            throw new IllegalStateException(
                    message,
                    lastError
            );
        }

        System.out.println(
                "AGENDA-SERVICE -> "
                        + message
        );
    }

    private void updatePasswordForExistingUser(
            String username,
            String password
    ) {

        try {

            registerUserKeycloakPort
                    .updatePassword(
                            username,
                            password,
                            false
                    );

            System.out.println(
                    "AGENDA-SERVICE -> Usuario ya existía en Keycloak. Contraseña sincronizada para: "
                            + username
            );

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "El usuario ya existía en Keycloak, pero no se pudo actualizar su contraseña: "
                            + username
                            + ". Detalle: "
                            + getMessage(ex),
                    ex
            );
        }
    }

    private void validateUserData(
            String username,
            String password,
            UserRole role
    ) {

        if (
                username == null
                        || username
                        .trim()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio para sincronizar con Keycloak."
            );
        }

        if (
                password == null
                        || password
                        .trim()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria para sincronizar con Keycloak."
            );
        }

        if (role == null) {

            throw new IllegalArgumentException(
                    "El rol es obligatorio para sincronizar con Keycloak."
            );
        }
    }

    private boolean isConflictForExistingUser(
            Exception ex
    ) {

        String messageText =
                getMessage(ex)
                        .toLowerCase();

        return messageText
                .contains("409")
                || messageText
                .contains("conflict")
                || messageText
                .contains("already exists")
                || messageText
                .contains("ya existe")
                || messageText
                .contains("user exists")
                || messageText
                .contains("exists");
    }

    private void waitBeforeRetry() {

        try {

            Thread.sleep(
                    RETRY_DELAY_MS
            );

        } catch (
                InterruptedException ex
        ) {

            Thread.currentThread()
                    .interrupt();
        }
    }

    private String getMessage(
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

        return messageBuilder
                .toString();
    }
}