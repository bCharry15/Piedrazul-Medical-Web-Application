package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.CreateAppointmentPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.AppointmentStatus;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;

@Component
public class CreateAppointmentPersistenceAdapter implements CreateAppointmentPort {

    private final AppointmentRepository appointmentRepository;

    public CreateAppointmentPersistenceAdapter(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Appointment createAppointment(
            Patient patient,
            Doctor doctor,
            LocalDate date,
            LocalTime time,
            String notes
    ) {
        boolean yaExisteAppointment = appointmentRepository.existsByDoctorAndDateAndTime(
                doctor,
                date,
                time
        );

        if (yaExisteAppointment) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una cita para el médico/terapista en la fecha y hora seleccionadas."
            );
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setNotes(notes);
        appointment.setStatus(AppointmentStatus.PROGRAMADA);

        return appointmentRepository.save(appointment);
    }
}
