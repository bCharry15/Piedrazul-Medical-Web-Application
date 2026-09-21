package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;

public interface SaveAppointmentPort {

    Appointment save(Appointment appointment);
}
