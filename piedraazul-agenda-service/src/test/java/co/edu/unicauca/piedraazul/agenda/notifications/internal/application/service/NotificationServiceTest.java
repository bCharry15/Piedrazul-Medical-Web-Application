package co.edu.unicauca.piedraazul.agenda.notifications.internal.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import co.edu.unicauca.piedraazul.agenda.appointments.api.event.AppointmentCreatedEvent;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out.NotificationLogPort;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out.SendEmailPort;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.domain.model.NotificationLog;

class NotificationServiceTest {

    @Mock
    private NotificationLogPort notificationLogPort;

    @Mock
    private SendEmailPort sendEmailPort;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        notificationService = new NotificationService(
                notificationLogPort,
                sendEmailPort);
    }

    @Test
    void shouldSaveSimulatedLogWhenEmailIsDisabled() {
        ReflectionTestUtils.setField(
                notificationService,
                "mailEnabled",
                false);

        AppointmentCreatedEvent event = createEvent();

        when(notificationLogPort.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.processAppointmentCreated(event);

        verify(sendEmailPort, never())
                .send(any(), any(), any());

        ArgumentCaptor<NotificationLog> captor =
                ArgumentCaptor.forClass(NotificationLog.class);

        verify(notificationLogPort).save(captor.capture());

        NotificationLog savedLog = captor.getValue();

        assertEquals("SIMULADO", savedLog.getStatus());
        assertEquals(10L, savedLog.getAppointmentId());
        assertEquals("Juan Perez", savedLog.getPatient());
        assertEquals("Dr. Gomez", savedLog.getDoctor());
        assertEquals("juan@example.com", savedLog.getPatientEmail());
    }

    @Test
    void shouldSendEmailAndSaveSentLogWhenEmailIsEnabled() {
        ReflectionTestUtils.setField(
                notificationService,
                "mailEnabled",
                true);

        AppointmentCreatedEvent event = createEvent();

        when(notificationLogPort.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.processAppointmentCreated(event);

        verify(sendEmailPort).send(
                "juan@example.com",
                "Confirmación de cita - Piedra Azul",
                "Hola Juan Perez,\n\n"
                        + "Tu cita fue agendada correctamente.\n\n"
                        + "Médico/Terapista: Dr. Gomez\n"
                        + "Fecha: 2026-09-20\n"
                        + "Hora: 10:00\n\n"
                        + "Gracias por usar el sistema Piedra Azul.");

        ArgumentCaptor<NotificationLog> captor =
                ArgumentCaptor.forClass(NotificationLog.class);

        verify(notificationLogPort).save(captor.capture());

        assertEquals(
                "ENVIADO",
                captor.getValue().getStatus());
    }

    private AppointmentCreatedEvent createEvent() {
        return new AppointmentCreatedEvent(
                10L,
                20L,
                "Juan Perez",
                "juan@example.com",
                "3001234567",
                30L,
                "Dr. Gomez",
                LocalDate.of(2026, 9, 20),
                LocalTime.of(10, 0));
    }
}