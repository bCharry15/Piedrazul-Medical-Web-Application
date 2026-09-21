package co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out;
import java.time.DayOfWeek;
import java.util.Optional;

import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

public interface FindDoctorAvailabilityPort {

    Optional<DoctorAvailability> findActiveAvailability(Doctor doctor, DayOfWeek dayOfWeek);
}
