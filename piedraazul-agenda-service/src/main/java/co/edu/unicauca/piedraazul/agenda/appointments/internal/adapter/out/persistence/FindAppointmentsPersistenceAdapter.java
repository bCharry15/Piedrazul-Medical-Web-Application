package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.FindAppointmentsPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;

@Component
public class FindAppointmentsPersistenceAdapter implements FindAppointmentsPort {

    private final AppointmentRepository appointmentRepository;

    public FindAppointmentsPersistenceAdapter(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Optional<Appointment> findById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    @Override
    public List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date) {
        return appointmentRepository.findByDoctorAndDateOrderByTimeAsc(doctor, date);
    }

    @Override
    public List<Appointment> findByPatientDocumentNumber(String documentNumber) {
        return appointmentRepository.findByPatientDocumentNumberOrderByDateAscTimeAsc(documentNumber);
    }

    @Override
    public boolean isTimeSlotOccupiedForAnotherAppointment(Doctor doctor,
                                                        LocalDate date,
                                                        LocalTime time,
                                                        Long appointmentId) {
        return appointmentRepository.existsByDoctorAndDateAndTimeAndIdNot(
                doctor,
                date,
                time,
                appointmentId
        );
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
}
