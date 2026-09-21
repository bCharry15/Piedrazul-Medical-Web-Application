package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in;
import java.util.Map;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.ChangeAppointmentStatusRequest;

public interface ChangeAppointmentStatusUseCase {

    Map<String, Object> changeStatus(Long appointmentId, ChangeAppointmentStatusRequest request);
}
