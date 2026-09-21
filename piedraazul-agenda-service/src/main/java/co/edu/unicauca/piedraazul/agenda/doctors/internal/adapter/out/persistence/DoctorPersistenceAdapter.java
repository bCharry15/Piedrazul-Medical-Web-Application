package co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence;
import java.util.Optional;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.FindDoctorPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence.DoctorRepository;

@Component
public class DoctorPersistenceAdapter implements FindDoctorPort {

    private final DoctorRepository doctorRepository;

    public DoctorPersistenceAdapter(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Optional<Doctor> findById(Long doctorId) {
        return doctorRepository.findByIdAndActiveTrue(doctorId);
    }
}
