package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

class ConfirmedAppointmentStateTest {

    @Test
    void shouldRepresentConfirmedAppointmentState() {

        ConfirmedAppointmentState state =
                new ConfirmedAppointmentState();

        assertEquals(
                AppointmentStatus.CONFIRMADA,
                state.getStatus()
        );

        assertFalse(
                state.canConfirm()
        );

        assertTrue(
                state.canAttend()
        );

        assertTrue(
                state.canCancel()
        );

        assertEquals(
                "La cita está confirmada y puede ser atendida o cancelada.",
                state.description()
        );
    }
}