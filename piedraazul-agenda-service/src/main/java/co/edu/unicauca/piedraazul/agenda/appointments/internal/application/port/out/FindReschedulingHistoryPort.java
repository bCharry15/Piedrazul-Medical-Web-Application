package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out;
import java.util.List;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;

public interface FindReschedulingHistoryPort {

    List<ReschedulingHistory> findByAppointmentId(Long appointmentId);
}
