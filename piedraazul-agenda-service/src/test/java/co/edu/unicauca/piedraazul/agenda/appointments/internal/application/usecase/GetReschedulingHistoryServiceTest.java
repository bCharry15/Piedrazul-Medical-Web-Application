package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindReschedulingHistoryPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;

@ExtendWith(MockitoExtension.class)
class GetReschedulingHistoryServiceTest {

    @Mock
    private FindAppointmentsPort findAppointmentsPort;

    @Mock
    private FindReschedulingHistoryPort findReschedulingHistoryPort;

    private GetReschedulingHistoryService service;

    @BeforeEach
    void setUp() {

        service =
                new GetReschedulingHistoryService(
                        findAppointmentsPort,
                        findReschedulingHistoryPort
                );
    }

    @Test
    void shouldRejectNullAppointmentId() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () ->
                                service.getByAppointmentId(
                                        null
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
                findReschedulingHistoryPort
        );
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() {

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
                                service.getByAppointmentId(
                                        99L
                                )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        assertTrue(
                exception.getReason()
                        .contains(
                                "99"
                        )
        );

        verify(
                findAppointmentsPort
        ).findById(
                99L
        );

        verifyNoInteractions(
                findReschedulingHistoryPort
        );
    }

    @Test
    void shouldReturnEmptyListWhenAppointmentHasNoHistory() {

        Appointment appointment =
                mock(
                        Appointment.class
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
                findReschedulingHistoryPort
                        .findByAppointmentId(
                                1L
                        )
        ).thenReturn(
                List.of()
        );

        List<Map<String, Object>> result =
                service.getByAppointmentId(
                        1L
                );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );

        verify(
                findAppointmentsPort
        ).findById(
                1L
        );

        verify(
                findReschedulingHistoryPort
        ).findByAppointmentId(
                1L
        );
    }

    @Test
    void shouldConvertReschedulingHistoryToMap() {

        Long appointmentId =
                1L;

        LocalDate previousDate =
                LocalDate.of(
                        2026,
                        9,
                        20
                );

        LocalTime previousTime =
                LocalTime.of(
                        9,
                        0
                );

        LocalDate newDate =
                LocalDate.of(
                        2026,
                        9,
                        25
                );

        LocalTime newTime =
                LocalTime.of(
                        11,
                        30
                );

        LocalDateTime changeDate =
                LocalDateTime.of(
                        2026,
                        9,
                        19,
                        15,
                        45
                );


        Appointment appointment =
                mock(
                        Appointment.class
                );

        when(
                appointment.getId()
        ).thenReturn(
                appointmentId
        );


        ReschedulingHistory history =
                mock(
                        ReschedulingHistory.class
                );

        when(
                history.getId()
        ).thenReturn(
                50L
        );

        when(
                history.getAppointment()
        ).thenReturn(
                appointment
        );

        when(
                history.getPreviousDate()
        ).thenReturn(
                previousDate
        );

        when(
                history.getPreviousTime()
        ).thenReturn(
                previousTime
        );

        when(
                history.getNewDate()
        ).thenReturn(
                newDate
        );

        when(
                history.getNewTime()
        ).thenReturn(
                newTime
        );

        when(
                history.getResponsible()
        ).thenReturn(
                "Agendador Principal"
        );

        when(
                history.getReason()
        ).thenReturn(
                "Solicitud del paciente"
        );

        when(
                history.getChangeDate()
        ).thenReturn(
                changeDate
        );


        when(
                findAppointmentsPort.findById(
                        appointmentId
                )
        ).thenReturn(
                Optional.of(
                        appointment
                )
        );

        when(
                findReschedulingHistoryPort
                        .findByAppointmentId(
                                appointmentId
                        )
        ).thenReturn(
                List.of(
                        history
                )
        );


        List<Map<String, Object>> result =
                service.getByAppointmentId(
                        appointmentId
                );


        assertNotNull(
                result
        );

        assertEquals(
                1,
                result.size()
        );


        Map<String, Object> response =
                result.get(
                        0
                );


        assertEquals(
                50L,
                response.get(
                        "id"
                )
        );

        assertEquals(
                appointmentId,
                response.get(
                        "appointmentId"
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
                "Agendador Principal",
                response.get(
                        "responsable"
                )
        );

        assertEquals(
                "Solicitud del paciente",
                response.get(
                        "motivo"
                )
        );

        assertEquals(
                changeDate,
                response.get(
                        "dateCambio"
                )
        );


        verify(
                findAppointmentsPort
        ).findById(
                appointmentId
        );

        verify(
                findReschedulingHistoryPort
        ).findByAppointmentId(
                appointmentId
        );
    }
}