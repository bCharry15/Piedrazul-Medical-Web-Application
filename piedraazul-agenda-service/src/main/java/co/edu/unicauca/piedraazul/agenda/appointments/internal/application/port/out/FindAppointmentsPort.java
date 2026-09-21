package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;

public interface FindAppointmentsPort {

    Optional<Appointment> findById(Long appointmentId);

    List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date);

    List<Appointment> findByPatientDocumentNumber(String documentNumber);

    boolean isTimeSlotOccupiedForAnotherAppointment(Doctor doctor,
                                                 LocalDate date,
                                                 LocalTime time,
                                                 Long appointmentId);

    Appointment save(Appointment appointment);
}
