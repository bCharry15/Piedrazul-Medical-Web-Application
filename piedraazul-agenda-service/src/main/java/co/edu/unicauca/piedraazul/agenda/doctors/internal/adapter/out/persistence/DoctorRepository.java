package co.edu.unicauca.piedraazul.agenda.doctors.internal.adapter.out.persistence;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByActiveTrueOrderByFullNameAsc();

    Optional<Doctor> findByIdAndActiveTrue(Long id);

    Optional<Doctor> findByUserUsername(String username);

    Optional<Doctor> findByUserUsernameAndActiveTrue(String username);
}
