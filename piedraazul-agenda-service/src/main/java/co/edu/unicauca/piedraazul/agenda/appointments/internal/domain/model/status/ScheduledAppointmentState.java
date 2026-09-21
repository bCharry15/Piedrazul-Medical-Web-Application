package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

public class ScheduledAppointmentState implements AppointmentStatusState {

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.PROGRAMADA;
    }

    @Override
    public boolean canConfirm() {
        return true;
    }

    @Override
    public boolean canAttend() {
        return false;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public String description() {
        return "La cita está programada y puede ser confirmada o cancelada.";
    }
}
