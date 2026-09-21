package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in;
import java.util.List;
import java.util.Map;

public interface GetReschedulingHistoryUseCase {

    List<Map<String, Object>> getByAppointmentId(Long appointmentId);
}
