package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out;
import java.time.LocalDate;
import java.time.LocalTime;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

public interface CreateAppointmentPort {

    Appointment createAppointment(
            Patient patient,
            Doctor doctor,
            LocalDate date,
            LocalTime time,
            String notes
    );
}
