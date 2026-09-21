package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in;
import java.util.List;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentResponse;

public interface GetPatientAppointmentsUseCase {

    List<AppointmentResponse> getByDocumentNumber(String documentNumber);
}
