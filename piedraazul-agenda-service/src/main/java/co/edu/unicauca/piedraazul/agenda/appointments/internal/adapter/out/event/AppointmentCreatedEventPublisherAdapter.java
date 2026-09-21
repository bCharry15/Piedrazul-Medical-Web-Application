package co.edu.unicauca.piedraazul.agenda.appointments.internal.adapter.out.event;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out.PublishAppointmentCreatedEventPort;
import co.edu.unicauca.piedraazul.agenda.appointments.api.event.AppointmentCreatedEvent;
import co.edu.unicauca.piedraazul.agenda.appointments.internal.domain.model.Appointment;
import co.edu.unicauca.piedraazul.agenda.doctors.internal.domain.model.Doctor;
import co.edu.unicauca.piedraazul.agenda.patients.internal.domain.model.Patient;

@Component
public class AppointmentCreatedEventPublisherAdapter implements PublishAppointmentCreatedEventPort {

    private final ApplicationEventPublisher eventPublisher;

    public AppointmentCreatedEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(Appointment createdAppointment) {
        Patient patient = createdAppointment.getPatient();
        Doctor doctor = createdAppointment.getDoctor();

        eventPublisher.publishEvent(new AppointmentCreatedEvent(
                createdAppointment.getId(),
                patient.getId(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                doctor.getId(),
                doctor.getFullName(),
                createdAppointment.getDate(),
                createdAppointment.getTime()
        ));
    }
}
