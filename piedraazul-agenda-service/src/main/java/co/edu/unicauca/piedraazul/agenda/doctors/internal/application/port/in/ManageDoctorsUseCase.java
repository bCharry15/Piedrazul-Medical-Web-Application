package co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.in;
import java.util.List;

import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.dto.DoctorRequest;

public interface ManageDoctorsUseCase {

    List<Doctor> listAll();

    Doctor createDoctor(DoctorRequest request);

    Doctor getById(Long doctorId);

    Doctor updateDoctor(Long doctorId, DoctorRequest request);

    void deleteDoctor(Long doctorId);
}
