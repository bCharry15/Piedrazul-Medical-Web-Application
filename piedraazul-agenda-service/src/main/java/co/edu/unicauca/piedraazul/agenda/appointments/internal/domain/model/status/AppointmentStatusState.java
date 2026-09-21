package co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.status;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;

public interface AppointmentStatusState {

    AppointmentStatus getStatus();

    boolean canConfirm();

    boolean canAttend();

    boolean canCancel();

    String description();
}
