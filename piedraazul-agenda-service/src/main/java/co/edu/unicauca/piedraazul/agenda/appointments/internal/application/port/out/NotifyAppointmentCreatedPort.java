package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.port.out;
import java.time.LocalDate;
import java.time.LocalTime;

public interface NotifyAppointmentCreatedPort {

    void notifyAppointmentCreated(
            Long appointmentId,
            String nombrePatient,
            String emailPatient,
            String phonePatient,
            String nombreDoctor,
            LocalDate date,
            LocalTime time
    );
}
