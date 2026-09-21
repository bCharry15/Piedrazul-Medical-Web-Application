package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

public class AttendedAppointmentState implements AppointmentStatusState {

    @Override
    public AppointmentStatus getStatus() {
        return AppointmentStatus.ATENDIDA;
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
        return "La cita ya fue atendida y no permite cambios de estado.";
    }
}
