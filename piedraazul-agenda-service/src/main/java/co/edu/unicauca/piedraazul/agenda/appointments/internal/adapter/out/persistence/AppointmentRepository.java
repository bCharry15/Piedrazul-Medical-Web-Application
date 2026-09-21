package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import jakarta.transaction.Transactional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorAndDateOrderByTimeAsc(Doctor doctor, LocalDate date);

    boolean existsByDoctorAndDateAndTime(Doctor doctor, LocalDate date, LocalTime time);

    boolean existsByDoctorAndDateAndTimeAndIdNot(Doctor doctor, LocalDate date, LocalTime time, Long id);

    boolean existsByDoctorId(Long doctorId);

    long countByDoctorId(Long doctorId);

    @Transactional
    void deleteByDoctorId(Long doctorId);

    List<Appointment> findByPatientDocumentNumberOrderByDateAscTimeAsc(String documentNumber);
}
