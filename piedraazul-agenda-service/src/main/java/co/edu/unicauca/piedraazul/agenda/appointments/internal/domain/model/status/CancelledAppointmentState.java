package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

public class CancelledAppointmentState implements AppointmentStatusState {

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.CANCELADA;
    }

    @Override
    public boolean canConfirm() {
        return false;
    }

    @Override
    public boolean canAttend() {
        return false;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public String description() {
        return "La cita fue cancelada y no permite nuevas transiciones.";
    }
}
