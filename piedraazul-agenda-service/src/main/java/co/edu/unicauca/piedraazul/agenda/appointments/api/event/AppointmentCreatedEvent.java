package co.edu.unicauca.piedraazul.agenda.appointments.api.event;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentCreatedEvent {

    private final Long appointmentId;
    private final Long patientId;
    private final String patient;
    private final String emailPatient;
    private final String phonePatient;
    private final Long doctorId;
    private final String doctor;
    private final LocalDate date;
    private final LocalTime time;

    public AppointmentCreatedEvent(Long appointmentId,
                           Long patientId,
                           String patient,
                           String emailPatient,
                           String phonePatient,
                           Long doctorId,
                           String doctor,
                           LocalDate date,
                           LocalTime time) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patient = patient;
        this.emailPatient = emailPatient;
        this.phonePatient = phonePatient;
        this.doctorId = doctorId;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatient() {
        return patient;
    }

    public String getPatientEmail() {
        return emailPatient;
    }

    public String getPatientPhone() {
        return phonePatient;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctor() {
        return doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }
}
