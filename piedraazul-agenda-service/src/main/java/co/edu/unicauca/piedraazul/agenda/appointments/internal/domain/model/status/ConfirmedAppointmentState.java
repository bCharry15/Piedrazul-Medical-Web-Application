package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

public class ConfirmedAppointmentState implements AppointmentStatusState {

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.CONFIRMADA;
    }

    @Override
    public boolean canConfirm() {
        return false;
    }

    @Override
    public boolean canAttend() {
        return true;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public String description() {
        return "La cita está confirmada y puede ser atendida o cancelada.";
    }
}
