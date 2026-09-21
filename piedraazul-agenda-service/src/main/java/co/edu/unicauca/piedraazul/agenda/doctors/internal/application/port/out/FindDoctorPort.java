package co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out;
import java.util.Optional;

import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

public interface FindDoctorPort {

    Optional<Doctor> findById(Long doctorId);
}
