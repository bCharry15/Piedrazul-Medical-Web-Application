package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentCommand;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.CreateAppointmentResponse;

public interface CreateAppointmentUseCase {

    CreateAppointmentResponse createAppointment(CreateAppointmentCommand command);
}
