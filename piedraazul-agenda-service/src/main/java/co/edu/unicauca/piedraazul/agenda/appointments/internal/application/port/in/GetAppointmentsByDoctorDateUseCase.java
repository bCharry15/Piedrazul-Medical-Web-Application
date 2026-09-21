package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.in;
import java.time.LocalDate;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto.AppointmentsByDoctorDateResponse;

public interface GetAppointmentsByDoctorDateUseCase {

    AppointmentsByDoctorDateResponse get(Long doctorId, LocalDate date);
}
