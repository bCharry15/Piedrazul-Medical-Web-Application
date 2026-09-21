package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentsByDoctorDateResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.FindDoctorPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

class GetAppointmentsByDoctorDateServiceTest {

    @Mock
    private FindDoctorPort findDoctorPort;

    @Mock
    private FindAppointmentsPort findAppointmentsPort;

    @Mock
    private Doctor doctor;

    @Mock
    private Patient patient;

    @Mock
    private Appointment appointment;

    private GetAppointmentsByDoctorDateService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new GetAppointmentsByDoctorDateService(
                findDoctorPort,
                findAppointmentsPort);
    }

    @Test
    void shouldReturnAppointmentsForDoctorAndDate() {
        LocalDate date =
                LocalDate.of(2026, 9, 20);

        when(findDoctorPort.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(findAppointmentsPort.findByDoctorAndDate(
                doctor,
                date))
                .thenReturn(List.of(appointment));

        when(doctor.getId())
                .thenReturn(1L);

        when(doctor.getFullName())
                .thenReturn("Jhoiner Puentes");

        when(patient.getId())
                .thenReturn(10L);

        when(patient.getFullName())
                .thenReturn("Patient Demo");

        when(appointment.getId())
                .thenReturn(100L);

        when(appointment.getPatient())
                .thenReturn(patient);

        when(appointment.getDoctor())
                .thenReturn(doctor);

        when(appointment.getDate())
                .thenReturn(date);

        when(appointment.getTime())
                .thenReturn(LocalTime.of(9, 0));

        when(appointment.getStatus())
                .thenReturn(AppointmentStatus.PROGRAMADA);

        when(appointment.getNotes())
                .thenReturn("First appointment");

        AppointmentsByDoctorDateResponse response =
                service.get(1L, date);

        assertEquals(1L, response.getDoctorId());
        assertEquals(
                "Jhoiner Puentes",
                response.getDoctor());

        assertEquals(
                date,
                response.getDate());

        assertEquals(
                1,
                response.getCount());

        assertEquals(
                1,
                response.getAppointments().size());

        AppointmentResponse result =
                response.getAppointments().get(0);

        assertEquals(
                100L,
                result.getId());

        assertEquals(
                10L,
                result.getPatientId());

        assertEquals(
                "Patient Demo",
                result.getPatient());

        assertEquals(
                1L,
                result.getDoctorId());

        assertEquals(
                "Jhoiner Puentes",
                result.getDoctor());

        assertEquals(
                LocalTime.of(9, 0),
                result.getTime());

        assertEquals(
                "PROGRAMADA",
                result.getStatus());

        assertEquals(
                "First appointment",
                result.getNotes());
    }

    @Test
    void shouldRejectNullDoctorId() {
        LocalDate date =
                LocalDate.of(2026, 9, 20);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.get(null, date));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verify(
                findDoctorPort,
                never())
                .findById(any());
    }

    @Test
    void shouldReturnNotFoundWhenDoctorDoesNotExist() {
        LocalDate date =
                LocalDate.of(2026, 9, 20);

        when(findDoctorPort.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.get(99L, date));

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode());

        verify(
                findAppointmentsPort,
                never())
                .findByDoctorAndDate(
                        any(),
                        any());
    }
}