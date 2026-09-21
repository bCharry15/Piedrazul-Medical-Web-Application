package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence;
import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.SaveAppointmentPort;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.persistence.AppointmentRepository;

@Component
public class AppointmentPersistenceAdapter implements SaveAppointmentPort {

    private final AppointmentRepository appointmentRepository;

    public AppointmentPersistenceAdapter(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
}
