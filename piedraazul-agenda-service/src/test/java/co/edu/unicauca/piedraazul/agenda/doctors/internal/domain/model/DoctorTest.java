package co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.User;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserRole;
import co.edu.unicauca.piedraazul.agenda.identity.internal.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Pruebas unitarias - Modelo Doctor")
class DoctorTest {

    private Doctor doctor;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("dr.lopez");
        user.setPassword("hashedpassword");
        user.setRole(UserRole.DOCTOR);
        user.setStatus(UserStatus.ACTIVE);

        doctor = new Doctor();
        doctor.setFullName("Dr. Juan López");
        doctor.setSpecialty("Neurología");
        doctor.setIntervalMinutes(45);
        doctor.setUser(user);
    }

    @Test
    @DisplayName("El toString del médico retorna nombre y especialidad")
    void toStringRetornaNombreYEspecialidad() {
        String result = doctor.toString();
        assertEquals("Dr. Juan López - Neurología", result);
    }

    @Test
    @DisplayName("El nombre completo del médico se almacena correctamente")
    void nombreCompletoAlmacenado() {
        assertEquals("Dr. Juan López", doctor.getFullName());
    }

    @Test
    @DisplayName("La especialidad del médico se almacena correctamente")
    void especialidadAlmacenada() {
        assertEquals("Neurología", doctor.getSpecialty());
    }

    @Test
    @DisplayName("El intervalo de minutos se almacena correctamente")
    void intervalMinutesAlmacenado() {
        assertEquals(45, doctor.getIntervalMinutes());
    }

    @Test
    @DisplayName("El user asociado al médico se almacena correctamente")
    void userAsociadoAlmacenado() {
        assertNotNull(doctor.getUser());
        assertEquals("dr.lopez", doctor.getUser().getUsername());
    }

    @Test
    @DisplayName("La especialidad puede updatese")
    void updateEspecialidad() {
        doctor.setSpecialty("Cardiología");
        assertEquals("Cardiología", doctor.getSpecialty());
    }

    @Test
    @DisplayName("El intervalo de minutos puede updatese")
    void updateIntervaloMinutos() {
        doctor.setIntervalMinutes(30);
        assertEquals(30, doctor.getIntervalMinutes());
    }

    @Test
    @DisplayName("El médico puede no tener user asociado (nulo)")
    void doctorSinUser() {
        doctor.setUser(null);
        assertNull(doctor.getUser());
    }
}
