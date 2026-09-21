package co.edu.unicauca.piedraazul.agenda.notifications.internal.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NotificationLogTest {

    @Test
    void shouldInitializeWithoutAssignedValues() {

        NotificationLog notification =
                new NotificationLog();

        assertNull(
                notification.getId()
        );

        assertNull(
                notification.getAppointmentId()
        );

        assertNull(
                notification.getPatient()
        );

        assertNull(
                notification.getDoctor()
        );

        assertNull(
                notification.getPatientEmail()
        );

        assertNull(
                notification.getPatientPhone()
        );

        assertNull(
                notification.getStatus()
        );

        assertNull(
                notification.getSentAt()
        );
    }

    @Test
    void shouldStoreNotificationInformation() {

        NotificationLog notification =
                new NotificationLog();

        LocalDateTime sentAt =
                LocalDateTime.of(
                        2026,
                        9,
                        20,
                        10,
                        30
                );

        notification.setAppointmentId(
                15L
        );

        notification.setPatient(
                "Carlos Perez"
        );

        notification.setDoctor(
                "Yeison Vela"
        );

        notification.setPatientEmail(
                "carlos@example.com"
        );

        notification.setPatientPhone(
                "3001234567"
        );

        notification.setStatus(
                "ENVIADO"
        );

        notification.setSentAt(
                sentAt
        );

        assertEquals(
                15L,
                notification.getAppointmentId()
        );

        assertEquals(
                "Carlos Perez",
                notification.getPatient()
        );

        assertEquals(
                "Yeison Vela",
                notification.getDoctor()
        );

        assertEquals(
                "carlos@example.com",
                notification.getPatientEmail()
        );

        assertEquals(
                "3001234567",
                notification.getPatientPhone()
        );

        assertEquals(
                "ENVIADO",
                notification.getStatus()
        );

        assertEquals(
                sentAt,
                notification.getSentAt()
        );
    }
}