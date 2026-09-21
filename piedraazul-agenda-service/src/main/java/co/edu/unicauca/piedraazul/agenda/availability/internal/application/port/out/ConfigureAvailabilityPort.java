package co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out;
import java.util.List;
import java.util.Optional;

import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

public interface ConfigureAvailabilityPort {

    DoctorAvailability save(DoctorAvailability availability);

    Optional<DoctorAvailability> findById(Long availabilityId);

    List<DoctorAvailability> findByDoctorAndActive(Doctor doctor);
}
