package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

class CancelledAppointmentStateTest {

    @Test
    void shouldRepresentCancelledAppointmentState() {

        CancelledAppointmentState state =
                new CancelledAppointmentState();

        assertEquals(
                AppointmentStatus.CANCELADA,
                state.getStatus()
        );

        assertFalse(
                state.canConfirm()
        );

        assertFalse(
                state.canAttend()
        );

        assertFalse(
                state.canCancel()
        );

        assertEquals(
                "La cita fue cancelada y no permite nuevas transiciones.",
                state.description()
        );
    }
}