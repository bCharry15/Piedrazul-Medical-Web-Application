package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

class AppointmentStatusStateServiceTest {

    private AppointmentStatusStateService service;

    @BeforeEach
    void setUp() {

        service =
                new AppointmentStatusStateService();
    }

    @Test
    void shouldReturnScheduledStatusWhenStatusIsNull() {

        AppointmentStatus result =
                service.getSafeStatus(
                        null
                );

        assertEquals(
                AppointmentStatus.PROGRAMADA,
                result
        );
    }

    @Test
    void shouldReturnSameStatusWhenStatusIsNotNull() {

        AppointmentStatus result =
                service.getSafeStatus(
                        AppointmentStatus.CONFIRMADA
                );

        assertEquals(
                AppointmentStatus.CONFIRMADA,
                result
        );
    }

    @Test
    void shouldIdentifyActiveStatuses() {

        assertTrue(
                service.isActiveStatus(
                        AppointmentStatus.PROGRAMADA
                )
        );

        assertTrue(
                service.isActiveStatus(
                        AppointmentStatus.CONFIRMADA
                )
        );

        assertTrue(
                service.isActiveStatus(
                        AppointmentStatus.PENDIENTE
                )
        );
    }

    @Test
    void shouldRejectNullAndFinalStatusesAsActive() {

        assertFalse(
                service.isActiveStatus(
                        null
                )
        );

        assertFalse(
                service.isActiveStatus(
                        AppointmentStatus.ATENDIDA
                )
        );

        assertFalse(
                service.isActiveStatus(
                        AppointmentStatus.COMPLETADA
                )
        );

        assertFalse(
                service.isActiveStatus(
                        AppointmentStatus.CANCELADA
                )
        );

        assertFalse(
                service.isActiveStatus(
                        AppointmentStatus.NO_VINO
                )
        );
    }

    @Test
    void shouldIdentifyFinalStatuses() {

        assertTrue(
                service.isFinalStatus(
                        AppointmentStatus.ATENDIDA
                )
        );

        assertTrue(
                service.isFinalStatus(
                        AppointmentStatus.COMPLETADA
                )
        );

        assertTrue(
                service.isFinalStatus(
                        AppointmentStatus.CANCELADA
                )
        );

        assertTrue(
                service.isFinalStatus(
                        AppointmentStatus.NO_VINO
                )
        );
    }

    @Test
    void shouldRejectNullAndActiveStatusesAsFinal() {

        assertFalse(
                service.isFinalStatus(
                        null
                )
        );

        assertFalse(
                service.isFinalStatus(
                        AppointmentStatus.PROGRAMADA
                )
        );

        assertFalse(
                service.isFinalStatus(
                        AppointmentStatus.CONFIRMADA
                )
        );

        assertFalse(
                service.isFinalStatus(
                        AppointmentStatus.PENDIENTE
                )
        );
    }
}