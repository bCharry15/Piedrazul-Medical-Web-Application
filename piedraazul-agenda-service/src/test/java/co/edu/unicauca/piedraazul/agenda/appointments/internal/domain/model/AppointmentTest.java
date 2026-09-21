package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Pruebas unitarias - Modelo Appointment")
class AppointmentTest {

    private Appointment appointment;
    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setFirstNames("Carlos");
        patient.setLastNames("Gómez");
        patient.setDocumentNumber("123456789");

        doctor = new Doctor();
        doctor.setFullName("Dr. López");
        doctor.setSpecialty("Cardiología");
        doctor.setIntervalMinutes(30);

        appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(LocalDate.of(2026, 6, 15));
        appointment.setTime(LocalTime.of(9, 0));
        appointment.setStatus(AppointmentStatus.PROGRAMADA);
        appointment.setNotes("Consulta de control");
    }

    @Test
    @DisplayName("Una appointment nueva tiene status PROGRAMADA por defecto al asignarlo")
    void appointmentTieneStatusScheduled() {
        assertEquals(AppointmentStatus.PROGRAMADA, appointment.getStatus());
    }

    @Test
    @DisplayName("Se puede change el status de la appointment a COMPLETADA")
    void changeStatusACompletada() {
        appointment.setStatus(AppointmentStatus.COMPLETADA);
        assertEquals(AppointmentStatus.COMPLETADA, appointment.getStatus());
    }

    @Test
    @DisplayName("Se puede change el status de la appointment a CANCELADA")
    void changeStatusACancelled() {
        appointment.setStatus(AppointmentStatus.CANCELADA);
        assertEquals(AppointmentStatus.CANCELADA, appointment.getStatus());
    }

    @Test
    @DisplayName("La appointment almacena correctamente al patient asignado")
    void appointmentGuardaPatientCorrectamente() {
        assertEquals(patient, appointment.getPatient());
        assertEquals("Carlos", appointment.getPatient().getFirstNames());
    }

    @Test
    @DisplayName("La appointment almacena correctamente al médico asignado")
    void appointmentGuardaDoctorCorrectamente() {
        assertEquals(doctor, appointment.getDoctor());
        assertEquals("Dr. López", appointment.getDoctor().getFullName());
    }

    @Test
    @DisplayName("La appointment almacena correctamente la date y la time")
    void appointmentGuardaDateYTimeCorrectamente() {
        assertEquals(LocalDate.of(2026, 6, 15), appointment.getDate());
        assertEquals(LocalTime.of(9, 0), appointment.getTime());
    }

    @Test
    @DisplayName("La appointment almacena correctamente la observación")
    void appointmentGuardaNotesCorrectamente() {
        assertEquals("Consulta de control", appointment.getNotes());
    }

    @Test
    @DisplayName("La observación puede ser nula")
    void notesPuedeSerNula() {
        appointment.setNotes(null);
        assertNull(appointment.getNotes());
    }

    @Test
    @DisplayName("La appointment puede update su date")
    void updateDate() {
        LocalDate nuevaDate = LocalDate.of(2026, 7, 20);
        appointment.setDate(nuevaDate);
        assertEquals(nuevaDate, appointment.getDate());
    }

    @Test
    @DisplayName("La appointment puede update su time")
    void updateTime() {
        LocalTime nuevaTime = LocalTime.of(14, 30);
        appointment.setTime(nuevaTime);
        assertEquals(nuevaTime, appointment.getTime());
    }
}
