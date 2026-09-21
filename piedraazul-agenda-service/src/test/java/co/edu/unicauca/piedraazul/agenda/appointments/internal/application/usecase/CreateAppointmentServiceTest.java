package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
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

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentCommand;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentResponse;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.CreateAppointmentPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.PublishAppointmentCreatedEventPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.FindDoctorAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.FindDoctorPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.application.port.out.GetOrCreatePatientPort;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

class CreateAppointmentServiceTest {

    @Mock
    private FindDoctorPort findDoctorPort;

    @Mock
    private FindDoctorAvailabilityPort findDoctorAvailabilityPort;

    @Mock
    private GetOrCreatePatientPort getOrCreatePatientPort;

    @Mock
    private CreateAppointmentPort createAppointmentPort;

    @Mock
    private FindAppointmentsPort findAppointmentsPort;

    @Mock
    private PublishAppointmentCreatedEventPort publishAppointmentCreatedEventPort;

    @Mock
    private Doctor doctor;

    @Mock
    private DoctorAvailability availability;

    @Mock
    private Patient patient;

    @Mock
    private Appointment appointment;

    private CreateAppointmentService service;

    private LocalDate appointmentDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new CreateAppointmentService(
                findDoctorPort,
                findDoctorAvailabilityPort,
                getOrCreatePatientPort,
                createAppointmentPort,
                findAppointmentsPort,
                publishAppointmentCreatedEventPort);

        appointmentDate = LocalDate.now().plusDays(7);
    }

    @Test
    void shouldCreateAppointmentAndPublishEvent() {
        CreateAppointmentCommand command = createValidCommand();

        when(findDoctorPort.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(doctor.getSpecialty())
                .thenReturn("Consulta General");

        configureValidAvailability();

        when(findAppointmentsPort.findByPatientDocumentNumber("1234567890"))
                .thenReturn(List.of());

        when(getOrCreatePatientPort.getOrCreatePatient(
                eq("1234567890"),
                eq("CC"),
                eq("Patient"),
                eq("Demo"),
                eq("3001234567"),
                any(),
                eq(LocalDate.of(2000, 1, 1)),
                eq("patient.demo@piedraazul.com")))
                .thenReturn(patient);

        when(patient.getDocumentNumber())
                .thenReturn("1234567890");

        when(patient.getFullName())
                .thenReturn("Patient Demo");

        when(createAppointmentPort.createAppointment(
                patient,
                doctor,
                appointmentDate,
                LocalTime.of(9, 0),
                "Internal event test"))
                .thenReturn(appointment);

        when(appointment.getId())
                .thenReturn(1L);

        when(appointment.getPatient())
                .thenReturn(patient);

        when(appointment.getDoctor())
                .thenReturn(doctor);

        when(appointment.getDate())
                .thenReturn(appointmentDate);

        when(appointment.getTime())
                .thenReturn(LocalTime.of(9, 0));

        when(appointment.getStatus())
                .thenReturn(AppointmentStatus.PROGRAMADA);

        when(doctor.getFullName())
                .thenReturn("Jhoiner Puentes");

        CreateAppointmentResponse response =
                service.createAppointment(command);

        assertNotNull(response);

        verify(createAppointmentPort).createAppointment(
                patient,
                doctor,
                appointmentDate,
                LocalTime.of(9, 0),
                "Internal event test");

        verify(publishAppointmentCreatedEventPort)
                .publish(appointment);
    }

    @Test
    void shouldRejectSpecializedAppointmentWithoutAttendedGeneralConsultation() {
        CreateAppointmentCommand command = createValidCommand();

        when(findDoctorPort.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(doctor.getSpecialty())
                .thenReturn("Terapista");

        configureValidAvailability();

        when(findAppointmentsPort.findByPatientDocumentNumber("1234567890"))
                .thenReturn(List.of());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createAppointment(command));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verify(getOrCreatePatientPort, never())
                .getOrCreatePatient(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any());

        verify(publishAppointmentCreatedEventPort, never())
                .publish(any());
    }

    @Test
    void shouldRejectAppointmentWhenDoctorDoesNotExist() {
        CreateAppointmentCommand command = createValidCommand();

        when(findDoctorPort.findById(1L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.createAppointment(command));

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode());

        verify(createAppointmentPort, never())
                .createAppointment(
                        any(),
                        any(),
                        any(),
                        any(),
                        any());

        verify(publishAppointmentCreatedEventPort, never())
                .publish(any());
    }

    private void configureValidAvailability() {
        DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();

        when(findDoctorAvailabilityPort.findActiveAvailability(
                doctor,
                dayOfWeek))
                .thenReturn(Optional.of(availability));

        when(availability.getWeekWindow())
                .thenReturn(4);

        when(availability.getStartTime())
                .thenReturn(LocalTime.of(9, 0));

        when(availability.getEndTime())
                .thenReturn(LocalTime.of(12, 0));

        when(availability.getIntervalMinutes())
                .thenReturn(15);
    }

    private CreateAppointmentCommand createValidCommand() {
        CreateAppointmentCommand command =
                new CreateAppointmentCommand();

        command.setDocumentNumber("1234567890");
        command.setDocumentType("CC");
        command.setFirstNames("Patient");
        command.setLastNames("Demo");
        command.setPhone("3001234567");
        command.setGender("HOMBRE");
        command.setBirthDate(LocalDate.of(2000, 1, 1));
        command.setEmail("patient.demo@piedraazul.com");
        command.setDoctorId(1L);
        command.setDate(appointmentDate);
        command.setTime(LocalTime.of(9, 0));
        command.setNotes("Internal event test");

        return command;
    }
}