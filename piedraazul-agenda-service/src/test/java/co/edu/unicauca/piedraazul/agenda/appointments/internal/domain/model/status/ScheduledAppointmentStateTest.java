package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

class ScheduledAppointmentStateTest {

    @Test
    void shouldRepresentScheduledAppointmentState() {

        ScheduledAppointmentState state =
                new ScheduledAppointmentState();

        assertEquals(
                AppointmentStatus.PROGRAMADA,
                state.getStatus()
        );

        assertTrue(
                state.canConfirm()
        );

        assertFalse(
                state.canAttend()
        );

        assertTrue(
                state.canCancel()
        );

        assertEquals(
                "La cita está programada y puede ser confirmada o cancelada.",
                state.description()
        );
    }
}