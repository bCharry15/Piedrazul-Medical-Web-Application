package co.edu.unicauca.piedraazul.agenda.identity.internal.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.in.ManageUsersUseCase;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.AuthenticateUserPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.EncodePasswordPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.ManageUsersPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.RegisterUserKeycloakPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;
import co.edu.unicauca.piedraazul.agenda.patients.api.PatientLookup;

@Service
public class ManageUsersService implements ManageUsersUseCase {

    private final ManageUsersPort manageUsersPort;
    private final EncodePasswordPort encodePasswordPort;
    private final AuthenticateUserPort authenticateUserPort;
    private final RegisterUserKeycloakPort registerUserKeycloakPort;
    private final SynchronizeUsersKeycloakService synchronizeUsersKeycloakService;
    private final PatientLookup patientLookup;

    public ManageUsersService(
            ManageUsersPort manageUsersPort,
            EncodePasswordPort encodePasswordPort,
            AuthenticateUserPort authenticateUserPort,
            RegisterUserKeycloakPort registerUserKeycloakPort,
            SynchronizeUsersKeycloakService synchronizeUsersKeycloakService,
            PatientLookup patientLookup) {

        this.manageUsersPort = manageUsersPort;
        this.encodePasswordPort = encodePasswordPort;
        this.authenticateUserPort = authenticateUserPort;
        this.registerUserKeycloakPort = registerUserKeycloakPort;
        this.synchronizeUsersKeycloakService = synchronizeUsersKeycloakService;
        this.patientLookup = patientLookup;
    }

    @Override
    public Map<String, Object> login(
            String username,
            String password) {

        validateRequiredText(
                username,
                "El nombre de usuario es obligatorio.");

        validateRequiredText(
                password,
                "La contraseña es obligatoria.");

        String normalizedUsername = username.trim();

        User localUser = manageUsersPort
                .findByUsername(normalizedUsername)
                .orElse(null);

        if (localUser != null
                && localUser.getStatus() == UserStatus.INACTIVE) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario se encuentra inactivo. Comuníquese con el administrador.");
        }

        Map<String, Object> keycloakToken =
                authenticateUserPort.getToken(
                        normalizedUsername,
                        password);

        Map<String, Object> response =
                new HashMap<>(keycloakToken);

        response.put(
                "username",
                normalizedUsername);

        response.put(
                "mensaje",
                "Autenticación exitosa con Keycloak.");

        if (localUser != null) {
            response.put(
                    "role",
                    localUser.getRole().name());

            response.put(
                    "status",
                    localUser.getStatus().name());
        }

        response.putIfAbsent(
                "role",
                "ADMIN");

        response.putIfAbsent(
                "status",
                "ACTIVE");

        return response;
    }

    @Override
    public Map<String, String> register(
            String username,
            String password,
            String role) {

        validateRequiredText(
                username,
                "El nombre de usuario es obligatorio.");

        validateRequiredText(
                password,
                "La contraseña es obligatoria.");

        validateRequiredText(
                role,
                "El rol es obligatorio.");

        String normalizedUsername =
                username.trim();

        if (manageUsersPort
                .findByUsername(normalizedUsername)
                .isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario ya existe.");
        }

        validateSecurePassword(password);

        UserRole userRole =
                convertRole(role);

        synchronizeUsersKeycloakService
                .synchronizeUserRequired(
                        normalizedUsername,
                        password,
                        userRole);

        User user = new User();

        user.setUsername(normalizedUsername);
        user.setPassword(
                encodePasswordPort.encode(password));
        user.setRole(userRole);
        user.setStatus(UserStatus.ACTIVE);

        manageUsersPort.save(user);

        return Map.of(
                "mensaje",
                "Usuario registrado correctamente y sincronizado con Keycloak.");
    }

    @Override
    public Map<String, String> generateTemporaryPassword(
            String username) {

        validateRequiredText(
                username,
                "El nombre de usuario es obligatorio.");

        manageUsersPort
                .findByUsername(username.trim())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe un usuario con ese nombre de usuario."));

        return Map.of(
                "mensaje",
                "Por seguridad, el sistema ya no entrega contraseñas temporales. Use el restablecimiento seguro.");
    }

    @Override
    public Map<String, String> resetPasswordSafely(
            String username,
            String documentNumber,
            String newPassword) {

        validateRequiredText(
                username,
                "El nombre de usuario es obligatorio.");

        validateRequiredText(
                documentNumber,
                "El número de documento es obligatorio.");

        validateRequiredText(
                newPassword,
                "La nueva contraseña es obligatoria.");

        validateSecurePassword(newPassword);

        String normalizedUsername =
                username.trim();

        String normalizedDocument =
                normalizeDocument(documentNumber);

        User user = manageUsersPort
                .findByUsername(normalizedUsername)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe un usuario con ese nombre de usuario."));

        if (user.getRole() == UserRole.PATIENT
                && !patientLookup.existsByDocumentNumber(
                        normalizedDocument)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de verificación no coinciden con un paciente registrado.");
        }

        registerUserKeycloakPort.updatePassword(
                normalizedUsername,
                newPassword,
                false);

        user.setPassword(
                encodePasswordPort.encode(newPassword));

        user.setStatus(UserStatus.ACTIVE);

        manageUsersPort.save(user);

        return Map.of(
                "mensaje",
                "La contraseña fue restablecida correctamente. Ya puede iniciar sesión con la nueva contraseña.");
    }

    @Override
    public List<User> listByRole(String role) {

        validateRequiredText(
                role,
                "El rol es obligatorio.");

        UserRole userRole =
                convertRole(role);

        return manageUsersPort.listByRole(userRole);
    }

    private void validateRequiredText(
            String value,
            String message) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    message);
        }
    }

    private void validateSecurePassword(String password) {

        if (password == null
                || password.trim().length() < 6) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener mínimo 6 caracteres.");
        }
    }

    private UserRole convertRole(String role) {

        try {
            return UserRole.valueOf(
                    role.trim().toUpperCase());

        } catch (Exception exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rol inválido. Valores permitidos: ADMIN, SCHEDULER, DOCTOR, PATIENT.");
        }
    }

    private String normalizeDocument(
            String documentNumber) {

        if (documentNumber == null) {
            return "";
        }

        return documentNumber
                .trim()
                .replaceAll(
                        "[^0-9A-Za-z]",
                        "");
    }
}