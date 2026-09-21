package co.edu.unicauca.piedraazul.agenda.availability.internal.adapter.out.persistence;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.piedraazul.agenda.availability.internal.domain.model.DoctorAvailability;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import jakarta.transaction.Transactional;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    Optional<DoctorAvailability> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, DayOfWeek dayOfWeek);

    Optional<DoctorAvailability> findFirstByDoctorAndDayOfWeekAndActiveTrueOrderByIdDesc(
            Doctor doctor,
            DayOfWeek dayOfWeek
    );

    List<DoctorAvailability> findByDoctorAndActiveTrue(Doctor doctor);

    List<DoctorAvailability> findByDoctorId(Long doctorId);

    @Transactional
    void deleteByDoctorId(Long doctorId);
}
