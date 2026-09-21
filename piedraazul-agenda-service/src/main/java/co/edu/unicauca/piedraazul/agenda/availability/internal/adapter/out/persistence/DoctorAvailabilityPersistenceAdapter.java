package co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence;
import java.time.DayOfWeek;
import java.util.Optional;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.availability.internal.application.port.out.FindDoctorAvailabilityPort;
import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence.DoctorAvailabilityRepository;

@Component
public class DoctorAvailabilityPersistenceAdapter implements FindDoctorAvailabilityPort {

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public DoctorAvailabilityPersistenceAdapter(DoctorAvailabilityRepository doctorAvailabilityRepository) {
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }

    @Override
    public Optional<DoctorAvailability> findActiveAvailability(Doctor doctor, DayOfWeek dayOfWeek) {
        return doctorAvailabilityRepository
                .findFirstByDoctorAndDayOfWeekAndActiveTrueOrderByIdDesc(doctor, dayOfWeek);
    }
}
