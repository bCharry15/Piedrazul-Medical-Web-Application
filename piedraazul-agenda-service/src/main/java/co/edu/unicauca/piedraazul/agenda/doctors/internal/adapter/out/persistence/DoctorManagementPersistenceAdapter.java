package co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.doctors.internal.application.port.out.ManageDoctorsPort;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence.DoctorRepository;

@Component
public class DoctorManagementPersistenceAdapter implements ManageDoctorsPort {

    private final DoctorRepository doctorRepository;

    public DoctorManagementPersistenceAdapter(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<Doctor> listAll() {
        return doctorRepository.findByActiveTrueOrderByFullNameAsc();
    }

    @Override
    public Optional<Doctor> findById(Long doctorId) {
        return doctorRepository.findByIdAndActiveTrue(doctorId);
    }

    @Override
    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}
