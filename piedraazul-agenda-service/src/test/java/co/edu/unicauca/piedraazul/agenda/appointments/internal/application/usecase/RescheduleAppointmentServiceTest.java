package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.RescheduleAppointmentRequest;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.SaveReschedulingHistoryPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

@ExtendWith(MockitoExtension.class)
class RescheduleAppointmentServiceTest {

    @Mock
    private FindAppointmentsPort findAppointmentsPort;

    @Mock
    private SaveReschedulingHistoryPort saveReschedulingHistoryPort;

    private RescheduleAppointmentService service;

    @BeforeEach
    void setUp() {

        service =
                new RescheduleAppointmentService(
                        findAppointmentsPort,
                        saveReschedulingHistoryPort
                );
    }

    @Test
    void shouldRejectNullAppointmentId() {

        RescheduleAppointmentRequest request =
                buildRequest(
                        LocalDate.now().plusDays(5),
                        LocalTime.of(11, 0),
                        "Paciente",
                        "Cambio solicitado"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        null,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertTrue(
                exception.getReason()
                        .contains(
                                "id de la cita"
                        )
        );

        verifyNoInteractions(
                findAppointmentsPort,
                saveReschedulingHistoryPort
        );
    }

    @Test
    void shouldRejectNullRequest() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        1L,
                                        null
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                findAppointmentsPort,
                saveReschedulingHistoryPort
        );
    }

    @Test
    void shouldRejectRequestWithoutNewDate() {

        RescheduleAppointmentRequest request =
                buildRequest(
                        null,
                        LocalTime.of(11, 0),
                        "Paciente",
                        "Cambio"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertTrue(
                exception.getReason()
                        .contains(
                                "nueva fecha"
                        )
        );

        verifyNoInteractions(
                findAppointmentsPort,
                saveReschedulingHistoryPort
        );
    }

    @Test
    void shouldRejectRequestWithoutNewTime() {

        RescheduleAppointmentRequest request =
                buildRequest(
                        LocalDate.now().plusDays(5),
                        null,
                        "Paciente",
                        "Cambio"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertTrue(
                exception.getReason()
                        .contains(
                                "nueva hora"
                        )
        );

        verifyNoInteractions(
                findAppointmentsPort,
                saveReschedulingHistoryPort
        );
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() {

        RescheduleAppointmentRequest request =
                buildRequest(
                        LocalDate.now().plusDays(5),
                        LocalTime.of(11, 0),
                        "Paciente",
                        "Cambio"
                );

        when(
                findAppointmentsPort.findById(
                        99L
                )
        ).thenReturn(
                Optional.empty()
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        99L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                saveReschedulingHistoryPort
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRejectAppointmentInFinalStatus() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.CANCELADA,
                        LocalDate.now().plusDays(2),
                        LocalTime.of(10, 0),
                        "Cita cancelada"
                );

        RescheduleAppointmentRequest request =
                buildRequest(
                        LocalDate.now().plusDays(5),
                        LocalTime.of(11, 0),
                        "Paciente",
                        "Cambio"
                );

        when(
                findAppointmentsPort.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        appointment
                )
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                saveReschedulingHistoryPort
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRejectSameDateAndTime() {

        LocalDate currentDate =
                LocalDate.now().plusDays(2);

        LocalTime currentTime =
                LocalTime.of(
                        10,
                        0
                );

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.PROGRAMADA,
                        currentDate,
                        currentTime,
                        "Consulta"
                );

        RescheduleAppointmentRequest request =
                buildRequest(
                        currentDate,
                        currentTime,
                        "Paciente",
                        "Cambio"
                );

        when(
                findAppointmentsPort.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        appointment
                )
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertTrue(
                exception.getReason()
                        .contains(
                                "iguales"
                        )
        );

        verifyNoInteractions(
                saveReschedulingHistoryPort
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRejectPastDate() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.PROGRAMADA,
                        LocalDate.now().plusDays(2),
                        LocalTime.of(10, 0),
                        "Consulta"
                );

        RescheduleAppointmentRequest request =
                buildRequest(
                        LocalDate.now().minusDays(1),
                        LocalTime.of(11, 0),
                        "Paciente",
                        "Cambio"
                );

        when(
                findAppointmentsPort.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        appointment
                )
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertTrue(
                exception.getReason()
                        .contains(
                                "fecha pasada"
                        )
        );

        verifyNoInteractions(
                saveReschedulingHistoryPort
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRejectOccupiedTimeSlot() {

        LocalDate newDate =
                LocalDate.now().plusDays(5);

        LocalTime newTime =
                LocalTime.of(
                        11,
                        30
                );

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.PROGRAMADA,
                        LocalDate.now().plusDays(2),
                        LocalTime.of(10, 0),
                        "Consulta"
                );

        RescheduleAppointmentRequest request =
                buildRequest(
                        newDate,
                        newTime,
                        "Paciente",
                        "Cambio"
                );

        when(
                findAppointmentsPort.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        appointment
                )
        );

        when(
                findAppointmentsPort
                        .isTimeSlotOccupiedForAnotherAppointment(
                                appointment.getDoctor(),
                                newDate,
                                newTime,
                                1L
                        )
        ).thenReturn(
                true
        );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.reschedule(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        assertTrue(
                exception.getReason()
                        .contains(
                                "ya tiene una cita"
                        )
        );

        verifyNoInteractions(
                saveReschedulingHistoryPort
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRescheduleAppointmentSuccessfully() {

        LocalDate previousDate =
                LocalDate.now().plusDays(2);

        LocalTime previousTime =
                LocalTime.of(
                        10,
                        0
                );

        LocalDate newDate =
                LocalDate.now().plusDays(5);

        LocalTime newTime =
                LocalTime.of(
                        11,
                        30
                );

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.PROGRAMADA,
                        previousDate,
                        previousTime,
                        "Dolor de tobillo"
                );

        RescheduleAppointmentRequest request =
                buildRequest(
                        newDate,
                        newTime,
                        "  Agendador   Principal  ",
                        "  Cambio   solicitado   por paciente  "
                );

        when(
                findAppointmentsPort.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        appointment
                )
        );

        when(
                findAppointmentsPort.save(
                        appointment
                )
        ).thenReturn(
                appointment
        );

        Map<String, Object> response =
                service.reschedule(
                        1L,
                        request
                );

        assertEquals(
                "Cita re-agendada correctamente.",
                response.get(
                        "mensaje"
                )
        );

        assertEquals(
                previousDate,
                response.get(
                        "dateAnterior"
                )
        );

        assertEquals(
                previousTime,
                response.get(
                        "timeAnterior"
                )
        );

        assertEquals(
                newDate,
                response.get(
                        "dateNueva"
                )
        );

        assertEquals(
                newTime,
                response.get(
                        "timeNueva"
                )
        );

        assertEquals(
                newDate,
                appointment.getDate()
        );

        assertEquals(
                newTime,
                appointment.getTime()
        );

        assertTrue(
                appointment.getNotes()
                        .contains(
                                "Dolor de tobillo"
                        )
        );

        assertTrue(
                appointment.getNotes()
                        .contains(
                                "Agendador Principal"
                        )
        );

        assertTrue(
                appointment.getNotes()
                        .contains(
                                "Cambio solicitado por paciente"
                        )
        );

        ArgumentCaptor<ReschedulingHistory>
                historyCaptor =
                ArgumentCaptor.forClass(
                        ReschedulingHistory.class
                );

        verify(
                saveReschedulingHistoryPort
        ).save(
                historyCaptor.capture()
        );

        ReschedulingHistory history =
                historyCaptor.getValue();

        assertNotNull(
                history
        );

        assertEquals(
                previousDate,
                history.getPreviousDate()
        );

        assertEquals(
                previousTime,
                history.getPreviousTime()
        );

        assertEquals(
                newDate,
                history.getNewDate()
        );

        assertEquals(
                newTime,
                history.getNewTime()
        );

        assertEquals(
                "Agendador Principal",
                history.getResponsible()
        );

        assertEquals(
                "Cambio solicitado por paciente",
                history.getReason()
        );

        verify(
                appointment
        ).setDate(
                newDate
        );

        verify(
                appointment
        ).setTime(
                newTime
        );

        verify(
                findAppointmentsPort
        ).save(
                appointment
        );
    }

    @Test
    void shouldUseDefaultResponsibleAndReason() {

        LocalDate previousDate =
                LocalDate.now().plusDays(2);

        LocalTime previousTime =
                LocalTime.of(
                        9,
                        0
                );

        LocalDate newDate =
                LocalDate.now().plusDays(6);

        LocalTime newTime =
                LocalTime.of(
                        9,
                        30
                );

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.CONFIRMADA,
                        previousDate,
                        previousTime,
                        null
                );

        RescheduleAppointmentRequest request =
                buildRequest(
                        newDate,
                        newTime,
                        null,
                        "   "
                );

        when(
                findAppointmentsPort.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(
                        appointment
                )
        );

        when(
                findAppointmentsPort.save(
                        appointment
                )
        ).thenReturn(
                appointment
        );

        service.reschedule(
                1L,
                request
        );

        ArgumentCaptor<ReschedulingHistory>
                historyCaptor =
                ArgumentCaptor.forClass(
                        ReschedulingHistory.class
                );

        verify(
                saveReschedulingHistoryPort
        ).save(
                historyCaptor.capture()
        );

        ReschedulingHistory history =
                historyCaptor.getValue();

        assertEquals(
                "Sistema",
                history.getResponsible()
        );

        assertEquals(
                "Re-agendamiento de cita",
                history.getReason()
        );

        String expectedNotes =
                "Re-agendada de "
                        + previousDate
                        + " "
                        + previousTime
                        + " a "
                        + newDate
                        + " "
                        + newTime
                        + ". Responsable: Sistema"
                        + ". Motivo: Re-agendamiento de cita";

        assertEquals(
                expectedNotes,
                appointment.getNotes()
        );
    }

    private RescheduleAppointmentRequest buildRequest(
            LocalDate newDate,
            LocalTime newTime,
            String responsible,
            String reason
    ) {

        RescheduleAppointmentRequest request =
                new RescheduleAppointmentRequest();

        request.setNewDate(
                newDate
        );

        request.setNewTime(
                newTime
        );

        request.setResponsible(
                responsible
        );

        request.setReason(
                reason
        );

        return request;
    }

    private Appointment buildAppointment(
            AppointmentStatus status,
            LocalDate initialDate,
            LocalTime initialTime,
            String initialNotes
    ) {

        Patient patient =
                mock(
                        Patient.class
                );

        lenient().when(
                patient.getId()
        ).thenReturn(
                10L
        );

        lenient().when(
                patient.getFullName()
        ).thenReturn(
                "Carlos Andres Perez Gomez"
        );

        lenient().when(
                patient.getDocumentNumber()
        ).thenReturn(
                "1000123456"
        );


        Doctor doctor =
                mock(
                        Doctor.class
                );

        lenient().when(
                doctor.getId()
        ).thenReturn(
                20L
        );

        lenient().when(
                doctor.getFullName()
        ).thenReturn(
                "Yeison Vela"
        );


        Appointment appointment =
                mock(
                        Appointment.class
                );

        AtomicReference<LocalDate>
                currentDate =
                new AtomicReference<>(
                        initialDate
                );

        AtomicReference<LocalTime>
                currentTime =
                new AtomicReference<>(
                        initialTime
                );

        AtomicReference<String>
                currentNotes =
                new AtomicReference<>(
                        initialNotes
                );


        lenient().when(
                appointment.getId()
        ).thenReturn(
                1L
        );

        lenient().when(
                appointment.getPatient()
        ).thenReturn(
                patient
        );

        lenient().when(
                appointment.getDoctor()
        ).thenReturn(
                doctor
        );

        lenient().when(
                appointment.getStatus()
        ).thenReturn(
                status
        );

        lenient().when(
                appointment.getDate()
        ).thenAnswer(
                invocation ->
                        currentDate.get()
        );

        lenient().when(
                appointment.getTime()
        ).thenAnswer(
                invocation ->
                        currentTime.get()
        );

        lenient().when(
                appointment.getNotes()
        ).thenAnswer(
                invocation ->
                        currentNotes.get()
        );


        lenient().doAnswer(
                invocation -> {

                    currentDate.set(
                            invocation.getArgument(
                                    0
                            )
                    );

                    return null;
                }
        ).when(
                appointment
        ).setDate(
                any(
                        LocalDate.class
                )
        );


        lenient().doAnswer(
                invocation -> {

                    currentTime.set(
                            invocation.getArgument(
                                    0
                            )
                    );

                    return null;
                }
        ).when(
                appointment
        ).setTime(
                any(
                        LocalTime.class
                )
        );


        lenient().doAnswer(
                invocation -> {

                    currentNotes.set(
                            invocation.getArgument(
                                    0
                            )
                    );

                    return null;
                }
        ).when(
                appointment
        ).setNotes(
                any(
                        String.class
                )
        );

        return appointment;
    }
}