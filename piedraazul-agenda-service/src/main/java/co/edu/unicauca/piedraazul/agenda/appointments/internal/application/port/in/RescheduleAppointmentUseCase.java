package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in;
import java.util.Map;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.RescheduleAppointmentRequest;

public interface RescheduleAppointmentUseCase {

    Map<String, Object> reschedule(Long appointmentId, RescheduleAppointmentRequest request);
}
