package co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Pruebas unitarias - Modelo Patient")
class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setDocumentNumber("987654321");
        patient.setDocumentType("CC");
        patient.setFirstNames("María");
        patient.setLastNames("Rodríguez");
        patient.setPhone("3001234567");
        patient.setGender(Gender.MUJER);
        patient.setBirthDate(LocalDate.of(1990, 3, 22));
        patient.setEmail("maria.rodriguez@email.com");
    }

    @Test
    @DisplayName("El nombre completo concatena nombres y apellidos correctamente")
    void nombreCompletoEsCorrecto() {
        assertEquals("María Rodríguez", patient.getFullName());
    }

    @Test
    @DisplayName("El número de document se almacena correctamente")
    void documentNumberCorrecto() {
        assertEquals("987654321", patient.getDocumentNumber());
    }

    @Test
    @DisplayName("El tipo de document se almacena correctamente")
    void documentTypeCorrecto() {
        assertEquals("CC", patient.getDocumentType());
    }

    @Test
    @DisplayName("El phone se almacena correctamente")
    void phoneCorrecto() {
        assertEquals("3001234567", patient.getPhone());
    }

    @Test
    @DisplayName("El género se almacena correctamente")
    void genderCorrecto() {
        assertEquals(Gender.MUJER, patient.getGender());
    }

    @Test
    @DisplayName("La date de nacimiento se almacena correctamente")
    void dateNacimientoCorrecto() {
        assertEquals(LocalDate.of(1990, 3, 22), patient.getBirthDate());
    }

    @Test
    @DisplayName("El email se almacena correctamente")
    void emailCorrecto() {
        assertEquals("maria.rodriguez@email.com", patient.getEmail());
    }

    @Test
    @DisplayName("El nombre completo con nombres compuestos funciona correctamente")
    void nombreCompletoConNombresCompuestos() {
        patient.setFirstNames("Ana María");
        patient.setLastNames("López Castro");
        assertEquals("Ana María López Castro", patient.getFullName());
    }

    @Test
    @DisplayName("Patient puede tener género HOMBRE")
    void patientConGenderHombre() {
        patient.setGender(Gender.HOMBRE);
        assertEquals(Gender.HOMBRE, patient.getGender());
    }

    @Test
    @DisplayName("Patient puede tener género OTRO")
    void patientConGenderOtro() {
        patient.setGender(Gender.OTRO);
        assertEquals(Gender.OTRO, patient.getGender());
    }

    @Test
    @DisplayName("El email puede ser nulo (campo opcional)")
    void emailPuedeSerNulo() {
        patient.setEmail(null);
        assertNull(patient.getEmail());
    }
}
