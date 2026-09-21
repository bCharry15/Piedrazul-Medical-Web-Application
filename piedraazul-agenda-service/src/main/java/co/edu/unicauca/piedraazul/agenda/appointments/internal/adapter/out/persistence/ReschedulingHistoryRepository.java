package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.ReschedulingHistory;
import jakarta.transaction.Transactional;

public interface ReschedulingHistoryRepository extends JpaRepository<ReschedulingHistory, Long> {

    List<ReschedulingHistory> findByAppointmentIdOrderByChangeDateDesc(Long appointmentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReschedulingHistory h WHERE h.appointment.doctor.id = :doctorId")
    void deleteByDoctorId(Long doctorId);
}
