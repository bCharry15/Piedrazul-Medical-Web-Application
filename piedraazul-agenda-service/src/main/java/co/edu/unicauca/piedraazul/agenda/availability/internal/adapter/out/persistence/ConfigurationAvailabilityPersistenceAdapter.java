package co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.ConfigureAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence.DoctorAvailabilityRepository;

@Component
public class ConfigurationAvailabilityPersistenceAdapter implements ConfigureAvailabilityPort {

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public ConfigurationAvailabilityPersistenceAdapter(DoctorAvailabilityRepository doctorAvailabilityRepository) {
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }

    @Override
    public DoctorAvailability save(DoctorAvailability availability) {
        return doctorAvailabilityRepository.save(availability);
    }

    @Override
    public Optional<DoctorAvailability> findById(Long availabilityId) {
        return doctorAvailabilityRepository.findById(availabilityId);
    }

    @Override
    public List<DoctorAvailability> findByDoctorAndActive(Doctor doctor) {
        return doctorAvailabilityRepository.findByDoctorAndActiveTrue(doctor);
    }
}
