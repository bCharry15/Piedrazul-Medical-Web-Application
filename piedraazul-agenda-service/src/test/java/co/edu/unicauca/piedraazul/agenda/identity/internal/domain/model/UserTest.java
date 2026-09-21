package co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;
import co.edu.unicauca.piedraazul.agenda.shared.observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas unitarias - Modelo User (con Observer)")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("scheduler01");
        user.setPassword("pass_encriptado");
        user.setRole(UserRole.SCHEDULER);
        user.setStatus(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("El username se almacena correctamente")
    void usernameCorrecto() {
        assertEquals("scheduler01", user.getUsername());
    }

    @Test
    @DisplayName("El rol SCHEDULER se asigna correctamente")
    void rolSchedulerAsignado() {
        assertEquals(UserRole.SCHEDULER, user.getRole());
    }

    @Test
    @DisplayName("El rol DOCTOR se puede asignar")
    void rolDoctorAsignado() {
        user.setRole(UserRole.DOCTOR);
        assertEquals(UserRole.DOCTOR, user.getRole());
    }

    @Test
    @DisplayName("El rol ADMIN se puede asignar")
    void rolAdminAsignado() {
        user.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("El status ACTIVE se almacena correctamente")
    void statusActiveCorrecto() {
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("El status INACTIVE se puede asignar")
    void statusInactiveAsignado() {
        user.setStatus(UserStatus.INACTIVE);
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Al change el status, los observers registereds son notificados")
    void observerEsNotificadoAlChangeStatus() {
        List<String> mensajesRecibidos = new ArrayList<>();

        Observer observadorDePrueba = mensajesRecibidos::add;
        user.attach(observadorDePrueba);

        user.setStatus(UserStatus.INACTIVE);

        assertEquals(1, mensajesRecibidos.size());
        assertTrue(mensajesRecibidos.get(0).contains("scheduler01"));
        assertTrue(mensajesRecibidos.get(0).contains("INACTIVE"));
    }

    @Test
    @DisplayName("Al detach de un observer, ya no recibe notifications")
    void observerDetachadoNoRecibeNotificationes() {
        List<String> mensajesRecibidos = new ArrayList<>();
        Observer observadorDePrueba = mensajesRecibidos::add;

        user.attach(observadorDePrueba);
        user.detach(observadorDePrueba);

        user.setStatus(UserStatus.INACTIVE);

        assertTrue(mensajesRecibidos.isEmpty());
    }

    @Test
    @DisplayName("Múltiples observers reciben la notificación al change status")
    void multiplesObserversRecibidos() {
        List<String> mensajes1 = new ArrayList<>();
        List<String> mensajes2 = new ArrayList<>();

        user.attach(mensajes1::add);
        user.attach(mensajes2::add);

        user.setStatus(UserStatus.INACTIVE);

        assertEquals(1, mensajes1.size());
        assertEquals(1, mensajes2.size());
    }

    @Test
    @DisplayName("notifyObservers envía el mensaje correcto a todos los observers")
    void notifyObserversEnviaMensajeCorrecto() {
        List<String> mensajes = new ArrayList<>();
        user.attach(mensajes::add);

        user.notifyObservers("Mensaje de prueba manual");

        assertEquals(1, mensajes.size());
        assertEquals("Mensaje de prueba manual", mensajes.get(0));
    }
}
