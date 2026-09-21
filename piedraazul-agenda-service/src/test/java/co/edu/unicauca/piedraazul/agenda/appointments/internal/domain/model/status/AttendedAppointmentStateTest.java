package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

class AttendedAppointmentStateTest {

    @Test
    void shouldRepresentAttendedAppointmentState() {

        AttendedAppointmentState state =
                new AttendedAppointmentState();

        assertEquals(
                AppointmentStatus.ATENDIDA,
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
                "La cita ya fue atendida y no permite cambios de estado.",
                state.description()
        );
    }
}