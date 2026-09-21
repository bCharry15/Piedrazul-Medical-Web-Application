package co.edu.unicauca.piedraazul.agenda.identity.internal.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.AuthenticateUserPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.EncodePasswordPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.ManageUsersPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.RegisterUserKeycloakPort;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;
import co.edu.unicauca.piedraazul.agenda.patients.api.PatientLookup;

class ManageUsersServiceTest {

    @Mock
    private ManageUsersPort manageUsersPort;

    @Mock
    private EncodePasswordPort encodePasswordPort;

    @Mock
    private AuthenticateUserPort authenticateUserPort;

    @Mock
    private RegisterUserKeycloakPort registerUserKeycloakPort;

    @Mock
    private SynchronizeUsersKeycloakService synchronizeUsersKeycloakService;

    @Mock
    private PatientLookup patientLookup;

    private ManageUsersService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new ManageUsersService(
                manageUsersPort,
                encodePasswordPort,
                authenticateUserPort,
                registerUserKeycloakPort,
                synchronizeUsersKeycloakService,
                patientLookup);
    }

    @Test
    void shouldRegisterUserAndSynchronizeWithKeycloak() {

        when(manageUsersPort.findByUsername("newuser"))
                .thenReturn(Optional.empty());

        when(encodePasswordPort.encode("secure123"))
                .thenReturn("encoded-password");

        Map<String, String> response =
                service.register(
                        " newuser ",
                        "secure123",
                        "PATIENT");

        verify(synchronizeUsersKeycloakService)
                .synchronizeUserRequired(
                        "newuser",
                        "secure123",
                        UserRole.PATIENT);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(manageUsersPort)
                .save(userCaptor.capture());

        User savedUser =
                userCaptor.getValue();

        assertEquals(
                "newuser",
                savedUser.getUsername());

        assertEquals(
                "encoded-password",
                savedUser.getPassword());

        assertEquals(
                UserRole.PATIENT,
                savedUser.getRole());

        assertEquals(
                UserStatus.ACTIVE,
                savedUser.getStatus());

        assertEquals(
                "Usuario registrado correctamente y sincronizado con Keycloak.",
                response.get("mensaje"));
    }

    @Test
    void shouldRejectDuplicateUsername() {

        User existingUser =
                new User();

        when(manageUsersPort.findByUsername("patient"))
                .thenReturn(Optional.of(existingUser));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.register(
                                "patient",
                                "secure123",
                                "PATIENT"));

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode());

        verify(
                synchronizeUsersKeycloakService,
                never())
                .synchronizeUserRequired(
                        any(),
                        any(),
                        any());

        verify(
                manageUsersPort,
                never())
                .save(any());
    }

    @Test
    void shouldRejectLoginForInactiveUser() {

        User inactiveUser =
                new User();

        inactiveUser.setUsername("patient");
        inactiveUser.setRole(UserRole.PATIENT);
        inactiveUser.setStatus(UserStatus.INACTIVE);

        when(manageUsersPort.findByUsername("patient"))
                .thenReturn(Optional.of(inactiveUser));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.login(
                                "patient",
                                "patient123"));

        assertEquals(
                HttpStatus.FORBIDDEN,
                exception.getStatusCode());

        verify(
                authenticateUserPort,
                never())
                .getToken(any(), any());
    }
}