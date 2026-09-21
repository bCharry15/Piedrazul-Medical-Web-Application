package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.lenient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.ChangeAppointmentStatusRequest;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

@ExtendWith(MockitoExtension.class)
class ChangeAppointmentStatusServiceTest {

    @Mock
    private FindAppointmentsPort findAppointmentsPort;

    private ChangeAppointmentStatusService service;

    @BeforeEach
    void setUp() {

        service =
                new ChangeAppointmentStatusService(
                        findAppointmentsPort
                );
    }

    @Test
    void shouldRejectNullAppointmentId() {

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "CONFIRMADA",
                        "Paciente confirmó asistencia"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.changeStatus(
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
                findAppointmentsPort
        );
    }

    @Test
    void shouldRejectNullRequest() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.changeStatus(
                                        1L,
                                        null
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                findAppointmentsPort
        );
    }

    @Test
    void shouldRejectEmptyStatus() {

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "   ",
                        "Observación"
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.changeStatus(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(
                findAppointmentsPort
        );
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() {

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "CONFIRMADA",
                        "Confirmación"
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
                                service.changeStatus(
                                        99L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRejectInvalidStatus() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.PROGRAMADA,
                        "Consulta general"
                );

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "ESTADO_INEXISTENTE",
                        "Prueba"
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
                                service.changeStatus(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRejectReturningAppointmentToScheduledStatus() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.CONFIRMADA,
                        "Cita confirmada"
                );

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "PROGRAMADA",
                        "Intento de retroceso"
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
                                service.changeStatus(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldRejectModificationWhenAppointmentIsAlreadyFinal() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.ATENDIDA,
                        "Consulta realizada"
                );

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "CANCELADA",
                        "Intento de modificación"
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
                                service.changeStatus(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(
                findAppointmentsPort,
                never()
        ).save(
                any()
        );
    }

    @Test
    void shouldChangeScheduledAppointmentToConfirmed() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.PROGRAMADA,
                        "Necesito una aprobación"
                );

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "confirmada",
                        "Consulta General confirmada"
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
                service.changeStatus(
                        1L,
                        request
                );

        assertEquals(
                AppointmentStatus.CONFIRMADA,
                appointment.getStatus()
        );

        assertEquals(
                "PROGRAMADA",
                response.get(
                        "statusAnterior"
                )
        );

        assertEquals(
                "CONFIRMADA",
                response.get(
                        "statusNuevo"
                )
        );

        assertEquals(
                "CONFIRMADA",
                response.get(
                        "status"
                )
        );

        assertEquals(
                "Estado de la cita actualizado correctamente.",
                response.get(
                        "mensaje"
                )
        );

        assertTrue(
                appointment.getNotes()
                        .contains(
                                "Necesito una aprobación"
                        )
        );

        assertTrue(
                appointment.getNotes()
                        .contains(
                                "Cambio de estado de PROGRAMADA a CONFIRMADA"
                        )
        );

        assertTrue(
                appointment.getNotes()
                        .contains(
                                "Consulta General confirmada"
                        )
        );

        verify(
                appointment
        ).setStatus(
                AppointmentStatus.CONFIRMADA
        );

        verify(
                findAppointmentsPort
        ).save(
                appointment
        );
    }

    @Test
    void shouldCreateStatusChangeNotesWhenPreviousNotesAreEmpty() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.CONFIRMADA,
                        null
                );

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "ATENDIDA",
                        "Consulta realizada correctamente"
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

        service.changeStatus(
                1L,
                request
        );

        assertEquals(
                AppointmentStatus.ATENDIDA,
                appointment.getStatus()
        );

        assertEquals(
                "Cambio de estado de CONFIRMADA a ATENDIDA. "
                        + "Observación: Consulta realizada correctamente",
                appointment.getNotes()
        );
    }

    @Test
    void shouldPreserveNotesWhenRequestDoesNotContainNewNotes() {

        Appointment appointment =
                buildAppointment(
                        AppointmentStatus.PROGRAMADA,
                        "Observación original"
                );

        ChangeAppointmentStatusRequest request =
                buildRequest(
                        "CONFIRMADA",
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

        service.changeStatus(
                1L,
                request
        );

        assertEquals(
                AppointmentStatus.CONFIRMADA,
                appointment.getStatus()
        );

        assertEquals(
                "Observación original",
                appointment.getNotes()
        );

        verify(
                appointment,
                never()
        ).setNotes(
                any()
        );
    }

    private ChangeAppointmentStatusRequest buildRequest(
            String status,
            String notes
    ) {

        ChangeAppointmentStatusRequest request =
                new ChangeAppointmentStatusRequest();

        request.setStatus(
                status
        );

        request.setNotes(
                notes
        );

        return request;
    }

    private Appointment buildAppointment(
        AppointmentStatus initialStatus,
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

    AtomicReference<AppointmentStatus>
            currentStatus =
            new AtomicReference<>(
                    initialStatus
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
            appointment.getDate()
    ).thenReturn(
            LocalDate.of(
                    2026,
                    9,
                    25
            )
    );

    lenient().when(
            appointment.getTime()
    ).thenReturn(
            LocalTime.of(
                    10,
                    30
            )
    );

    lenient().when(
            appointment.getStatus()
    ).thenAnswer(
            invocation ->
                    currentStatus.get()
    );

    lenient().doAnswer(
            invocation -> {

                currentStatus.set(
                        invocation.getArgument(
                                0
                        )
                );

                return null;
            }
    ).when(
            appointment
    ).setStatus(
            any(
                    AppointmentStatus.class
            )
    );

    lenient().when(
            appointment.getNotes()
    ).thenAnswer(
            invocation ->
                    currentNotes.get()
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
