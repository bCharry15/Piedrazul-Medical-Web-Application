package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;

@DisplayName("Pruebas unitarias - Enums del dominio")
class EnumsTest {

    // ─── AppointmentStatus ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("AppointmentStatus contiene exactamente los 7 estados esperados")
    void appointmentStatusHasExpectedValues() {
        AppointmentStatus[] valores = AppointmentStatus.values();
        assertArrayEquals(new AppointmentStatus[]{
                AppointmentStatus.PROGRAMADA,
                AppointmentStatus.CONFIRMADA,
                AppointmentStatus.PENDIENTE,
                AppointmentStatus.ATENDIDA,
                AppointmentStatus.COMPLETADA,
                AppointmentStatus.CANCELADA,
                AppointmentStatus.NO_VINO
        }, valores);
    }

    @Test
    @DisplayName("AppointmentStatus.PROGRAMADA existe y es accesible por nombre")
    void appointmentStatusProgramadaExists() {
        assertEquals(AppointmentStatus.PROGRAMADA, AppointmentStatus.valueOf("PROGRAMADA"));
    }

    @Test
    @DisplayName("AppointmentStatus.COMPLETADA existe y es accesible por nombre")
    void appointmentStatusCompletadaExists() {
        assertEquals(AppointmentStatus.COMPLETADA, AppointmentStatus.valueOf("COMPLETADA"));
    }

    @Test
    @DisplayName("AppointmentStatus.CANCELADA existe y es accesible por nombre")
    void appointmentStatusCanceladaExists() {
        assertEquals(AppointmentStatus.CANCELADA, AppointmentStatus.valueOf("CANCELADA"));
    }

    @Test
    @DisplayName("AppointmentStatus.valueOf lanza excepción para valores desconocidos")
    void statusAppointment_valorInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> AppointmentStatus.valueOf("UNKNOWN_STATUS"));
    }

    // ─── Gender ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Gender contiene exactamente los 3 valores esperados")
    void gender_tieneLosTresValores() {
        assertEquals(3, Gender.values().length);
    }

    @Test
    @DisplayName("Gender.HOMBRE existe")
    void gender_hombreExiste() {
        assertEquals(Gender.HOMBRE, Gender.valueOf("HOMBRE"));
    }

    @Test
    @DisplayName("Gender.MUJER existe")
    void gender_mujerExiste() {
        assertEquals(Gender.MUJER, Gender.valueOf("MUJER"));
    }

    @Test
    @DisplayName("Gender.OTRO existe")
    void gender_otroExiste() {
        assertEquals(Gender.OTRO, Gender.valueOf("OTRO"));
    }

    // ─── UserRole ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("UserRole contiene exactamente los 4 roles esperados")
    void userRole_tieneCuatroRoles() {
        assertEquals(4, UserRole.values().length);
    }

    @Test
    @DisplayName("UserRole.ADMIN existe")
    void userRole_adminExiste() {
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
    }

    @Test
    @DisplayName("UserRole.SCHEDULER existe")
    void userRole_schedulerExiste() {
        assertEquals(UserRole.SCHEDULER, UserRole.valueOf("SCHEDULER"));
    }

    @Test
    @DisplayName("UserRole.DOCTOR existe")
    void userRole_doctorExiste() {
        assertEquals(UserRole.DOCTOR, UserRole.valueOf("DOCTOR"));
    }

    @Test
    @DisplayName("UserRole.PATIENT existe")
    void userRole_patientExiste() {
        assertEquals(UserRole.PATIENT, UserRole.valueOf("PATIENT"));
    }

    // ─── UserStatus ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("UserStatus contiene exactamente los 2 statuss esperados")
    void userStatus_tieneDoStatuss() {
        assertEquals(2, UserStatus.values().length);
    }

    @Test
    @DisplayName("UserStatus.ACTIVE existe")
    void userStatus_activeExiste() {
        assertEquals(UserStatus.ACTIVE, UserStatus.valueOf("ACTIVE"));
    }

    @Test
    @DisplayName("UserStatus.INACTIVE existe")
    void userStatus_inactiveExiste() {
        assertEquals(UserStatus.INACTIVE, UserStatus.valueOf("INACTIVE"));
    }
}
