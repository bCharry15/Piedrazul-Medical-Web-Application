package co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out;
import java.util.List;
import java.util.Optional;

import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

public interface ManageDoctorsPort {

    List<Doctor> listAll();

    Optional<Doctor> findById(Long doctorId);

    Doctor save(Doctor doctor);
}
